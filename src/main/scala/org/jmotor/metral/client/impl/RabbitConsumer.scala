package org.jmotor.metral.client.impl

import java.util.concurrent.Callable

import com.google.common.cache.{ Cache, CacheBuilder }
import com.google.common.eventbus.EventBus
import com.google.protobuf.{ AbstractMessage, AbstractParser }
import com.rabbitmq.client.AMQP.Queue
import com.rabbitmq.client.{ AMQP, DefaultConsumer, Envelope }
import com.typesafe.config.Config
import org.jmotor.metral.client.Consumer

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
class RabbitConsumer(config: Config) extends RabbitClient(config) with Consumer {
  private[this] val queues: Cache[String, Queue.BindOk] = CacheBuilder.newBuilder().build()
  private[this] val parsers: Cache[String, AbstractParser[AbstractMessage]] = CacheBuilder.newBuilder().build()

  override def subscribe(queue: String, eventBus: EventBus): Unit = {
    val channel = getChannel
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        parseMessage(properties, body).foreach(message ⇒ eventBus.post(message))
      }
    }
    channel.basicQos(1)
    channel.basicConsume(queue, true, consumer)
  }

  override def bind(exchange: String, queue: String, routing: String, durable: Boolean): Unit = {
    val channel = getChannel
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
