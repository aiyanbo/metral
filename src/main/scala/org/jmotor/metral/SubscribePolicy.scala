package org.jmotor.metral

/**
 * Component:
 * Description:
 * Date: 2018-11-27
 *
 * @author AI
 */
trait SubscribePolicy {

  def isGlobal: Boolean

  def isDurable: Boolean

}

object SubscribePolicy {

  lazy val GLOBAL: SubscribePolicy = new SubscribePolicy {
    override def isGlobal: Boolean = true

    override def isDurable: Boolean = true
  }

  lazy val INSTANCE: SubscribePolicy = new SubscribePolicy {
    override def isGlobal: Boolean = false

    override def isDurable: Boolean = false
  }

  lazy val INSTANCE_DURABLE: SubscribePolicy = new SubscribePolicy {
    override def isGlobal: Boolean = false

    override def isDurable: Boolean = true
  }

}
