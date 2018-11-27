package org.jmotor.metral.internal

import java.net.InetAddress
import java.util.UUID

import com.google.common.eventbus.Subscribe
import com.typesafe.config.Config
import org.jmotor.metral.{ MessageCentral, SubscribePolicy }
import org.jmotor.metral.client.impl.{ RabbitConsumer, RabbitProducer }
import org.jmotor.metral.client.{ ExchangeType, Producer }
import org.jmotor.metral.dto.FireChanged
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

  override def subscribeFireChange(entity: String, obj: AnyRef, policy: SubscribePolicy): Unit = {
    val queue = getQueueName(entity, policy)
    consumer.bind(fireChangeExchange, queue, entity, policy.isDurable)
    consumer.subscribe(queue, EventBuses.FIRE_CHANGE_RECEIVER)
    EventBuses.FIRE_CHANGE_RECEIVER.register(obj)
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

  private[internal] def getQueueName(entity: String, policy: SubscribePolicy): String = {
    val queue = if (policy.isGlobal) s"$namespace.fire-changes.$entity" else s"$namespace.$hostname.fire-changes.$entity"
    if (policy.isDurable) "ha." + queue else queue
  }

  class FireChangeRecorder(producer: Producer) {

    @Subscribe def handleFireChange(e: FireChanged): Unit = {
      Retryable.retry(() ⇒ producer.send(fireChangeExchange, e.getEntity, UUID.randomUUID().toString, e))(maxAttempts)
    }

  }

}
