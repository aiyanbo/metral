package org.jmotor.metral

import com.typesafe.config.{ Config, ConfigFactory }
import org.jmotor.metral.api.{ Exchange, MessageHandler }
import org.jmotor.metral.dto.Message
import org.jmotor.metral.internal.DefaultMessageCentral

/**
 * Component:
 * Description:
 * Date: 2018/11/15
 *
 * @author AI
 */
trait MessageCentral {

  def declare(exchange: Exchange): Unit

  def send(exchange: String, message: Message): Unit

  def subscribeFireChange(entity: String, obj: AnyRef, policy: SubscribePolicy): Unit

  def subscribe(exchange: String, topic: String, policy: SubscribePolicy, handler: MessageHandler): Unit

  def shutdown(): Unit

}

object MessageCentral {

  def apply(): MessageCentral = apply(ConfigFactory.load())

  def apply(config: Config): MessageCentral = new DefaultMessageCentral(config).init()

}
