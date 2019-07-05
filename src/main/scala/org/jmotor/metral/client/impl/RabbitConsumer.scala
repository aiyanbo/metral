package org.jmotor.metral.client.impl

import java.util.concurrent.Callable

import com.google.common.cache.{ Cache, CacheBuilder }
import com.google.common.eventbus.EventBus
import com.google.protobuf.{ AbstractMessage, AbstractParser }
import com.rabbitmq.client.AMQP.Queue
import com.rabbitmq.client.{ AMQP, DefaultConsumer, Envelope }
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.jmotor.metral.api.{ Acknowledge, MessageHandler }
import org.jmotor.metral.client.Consumer
import org.jmotor.metral.dto.Message
import org.jmotor.metral.utils.Retryable

import scala.concurrent.duration._
import scala.util.control.NonFatal

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
class RabbitConsumer(config: Config) extends RabbitClient(config) with Consumer with LazyLogging {
  private[this] val maxAttempts = 100
  private[this] val retryDuration = 10.seconds
  private[this] val queues: Cache[String, Queue.BindOk] = CacheBuilder.newBuilder().build()
  private[this] val parsers: Cache[String, AbstractParser[AbstractMessage]] = CacheBuilder.newBuilder().build()

  override def subscribe(queue: String, eventBus: EventBus): Unit = {
    val channel = getOrCreateChannel
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        parseMessage(properties, body).foreach(message ⇒ eventBus.post(message))
      }
    }
    Retryable.retryDuration(() ⇒ {
      channel.basicQos(1)
      channel.basicConsume(queue, true, consumer)
    })(retryDuration, maxAttempts)
  }

  override def subscribe(queue: String, handler: MessageHandler): Unit = {
    val channel = getOrCreateChannel
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        val deliveryTag = envelope.getDeliveryTag
        try {
          parseMessage(properties, body).foreach { message ⇒
            handler.handle(message.asInstanceOf[Message], new Acknowledge {
              override def ack(): Unit = {
                getOrCreateChannel.basicAck(deliveryTag, false)
              }
            })
          }
        } catch {
          case NonFatal(t) ⇒ logger.error(t.getLocalizedMessage, t)
        }
      }
    }
    Retryable.retryDuration(() ⇒ {
      channel.basicQos(1)
      channel.basicConsume(queue, false, consumer)
    })(retryDuration, maxAttempts)
  }

  override def bind(exchange: String, queue: String, routing: String, durable: Boolean): Unit = {
    val channel = getOrCreateChannel
    queues.get(queue, new Callable[Queue.BindOk] {
      override def call(): Queue.BindOk = {
        channel.queueDeclare(queue, durable, false, !durable, null)
        channel.queueBind(queue, exchange, routing)
      }
    })
  }

  private[impl] def parseMessage(properties: AMQP.BasicProperties, body: Array[Byte]): Option[AbstractMessage] = {
    Option(properties.getType).map { typ ⇒
      val parser = parsers.get(typ, new Callable[AbstractParser[AbstractMessage]] {
        override def call(): AbstractParser[AbstractMessage] = {
          Class.forName(typ).getMethod("parser").invoke(null).asInstanceOf[AbstractParser[AbstractMessage]]
        }
      })
      parser.parseFrom(body)
    }
  }

}
