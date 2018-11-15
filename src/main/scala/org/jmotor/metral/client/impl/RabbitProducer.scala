package org.jmotor.metral.client.impl

import java.util.concurrent.Callable

import com.google.common.cache.{ Cache, CacheBuilder }
import com.google.protobuf.AbstractMessage
import com.rabbitmq.client.AMQP.{ BasicProperties, Exchange }
import com.rabbitmq.client.BuiltinExchangeType
import com.typesafe.config.Config
import org.jmotor.metral.client.ExchangeType.ExchangeType
import org.jmotor.metral.client.Producer

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
class RabbitProducer(config: Config) extends RabbitClient(config) with Producer {

  private[this] val exchanges: Cache[String, Exchange.DeclareOk] = CacheBuilder.newBuilder().build()

  override def declare(exchange: String, typ: ExchangeType): Unit = {
    val channel = getChannel
    exchanges.get(exchange, new Callable[Exchange.DeclareOk] {
      override def call(): Exchange.DeclareOk = {
        val exchangeType = BuiltinExchangeType.valueOf(typ.toString)
        channel.exchangeDeclare(exchange, exchangeType, true, false, None.orNull)
      }
    })
  }

  override def send(exchange: String, key: String, message: AbstractMessage): Unit = {
    doSend(exchange, key, message)
  }

  override def send(exchange: String, routing: String, key: String, message: AbstractMessage): Unit = {
    doSend(exchange, key, message, Option(routing))
  }

  private[impl] def doSend(exchange: String, key: String, message: AbstractMessage, routing: Option[String] = None): Unit = {
    val channel = getChannel
    val proto = message.getDescriptorForType.getFullName
    val properties = new BasicProperties.Builder().messageId(key)
      .contentType("application/protobuf").`type`(proto).build()
    channel.basicPublish(exchange, routing.getOrElse(""), false, false, properties, message.toByteArray)
  }

}
