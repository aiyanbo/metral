package org.jmotor.metral

import com.typesafe.config.{ Config, ConfigFactory }
import org.jmotor.metral.internal.DefaultMessageCentral

/**
 * Component:
 * Description:
 * Date: 2018/11/15
 *
 * @author AI
 */
trait MessageCentral {

  def subscribeFireChange(entity: String, obj: AnyRef, global: Boolean): Unit

  def shutdown(): Unit

}

object MessageCentral {

  def apply(): MessageCentral = apply(ConfigFactory.load())

  def apply(config: Config): MessageCentral = new DefaultMessageCentral(config).init()

}
