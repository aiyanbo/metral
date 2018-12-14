package org.jmotor.metral.internal

import java.net.InetAddress
import java.util.UUID

import com.google.common.eventbus.Subscribe
import com.typesafe.config.Config
import org.jmotor.metral.api.{ Exchange, MessageHandler }
import org.jmotor.metral.{ MessageCentral, SubscribePolicy }
import org.jmotor.metral.client.impl.{ RabbitConsumer, RabbitProducer }
import org.jmotor.metral.client.{ ExchangeType, Producer }
import org.jmotor.metral.dto.{ FireChanged, Message }
import org.jmotor.metral.utils.Retryable

import scala.util.Try

/**
 * Component:
 * Description:
 * Date: 2018/11/15
 *
 * @author AI
 */
class DefaultMessageCentral(config: Config) extends MessageCentral {

  private[this] lazy val maxAttempts = 100
  private[this] val fireChangeExchange = "metral.fire-changes"
  private[this] lazy val producer = new RabbitProducer(config)
  private[this] lazy val consumer = new RabbitConsumer(config)
  private[this] lazy val hostname = InetAddress.getLocalHost.getHostName
  private[this] lazy val namespace: String = config.getString("metral.namespace")

  override def declare(exchange: Exchange): Unit = {
    producer.declare(exchange.name, exchange.typ)
  }

  override def send(exchange: String, message: Message): Unit = {
    producer.send(exchange, message.getTopic, message.getId, message)
  }

  override def subscribeFireChange(entity: String, obj: AnyRef, policy: SubscribePolicy): Unit = {
    val queue = getFireChangeQueueName(entity, policy)
    consumer.bind(fireChangeExchange, queue, entity, policy.isDurable)
    consumer.subscribe(queue, EventBuses.FIRE_CHANGE_RECEIVER)
    EventBuses.FIRE_CHANGE_RECEIVER.register(obj)
  }

  override def subscribe(exchange: String, topic: String, policy: SubscribePolicy, handler: MessageHandler): Unit = {
    val queue = getQueueName(exchange, topic, policy)
    consumer.bind(exchange, queue, topic, policy.isDurable)
    consumer.subscribe(queue, handler)
  }

  override def shutdown(): Unit = {
    Try(consumer.close())
    Try(producer.close())
  }

  def init(): DefaultMessageCentral = {
    producer.declare(fireChangeExchange, ExchangeType.DIRECT)
    EventBuses.FIRE_CHANGE_SENDER.register(new FireChangeRecorder(producer))
    this
  }

  private[internal] def getFireChangeQueueName(entity: String, policy: SubscribePolicy): String = {
    val queue = if (policy.isGlobal) s"$namespace.fire-changes.$entity" else s"$namespace.$hostname.fire-changes.$entity"
    if (policy.isDurable) "ha." + queue else queue
  }

  private[internal] def getQueueName(exchange: String, topic: String, policy: SubscribePolicy): String = {
    val queue = if (policy.isGlobal) s"$namespace.$exchange.$topic" else s"$namespace.$hostname.$exchange.$topic"
    if (policy.isDurable) "ha." + queue else queue
  }

  class FireChangeRecorder(producer: Producer) {

    @Subscribe def handleFireChange(e: FireChanged): Unit = {
      Retryable.retry(() ⇒ producer.send(fireChangeExchange, e.getEntity, UUID.randomUUID().toString, e))(maxAttempts)
    }

  }

}
