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

  lazy final val GLOBAL: SubscribePolicy = new SubscribePolicy {
    override def isGlobal: Boolean = true

    override def isDurable: Boolean = true
  }

  lazy final val INSTANCE: SubscribePolicy = new SubscribePolicy {
    override def isGlobal: Boolean = false

    override def isDurable: Boolean = false
  }

  lazy final val INSTANCE_DURABLE: SubscribePolicy = new SubscribePolicy {
    override def isGlobal: Boolean = false

    override def isDurable: Boolean = true
  }

}
