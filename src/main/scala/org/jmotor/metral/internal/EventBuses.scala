package org.jmotor.metral.internal

import com.google.common.eventbus.EventBus

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
object EventBuses {

  lazy final val FIRE_CHANGE_SENDER = new EventBus("fire-changes-sender")

  lazy final val FIRE_CHANGE_RECEIVER = new EventBus("fire-changes-receiver")

}
