package org.jmotor.metral.internal

import java.util.concurrent.atomic.AtomicBoolean

import com.google.common.eventbus.EventBus

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
object EventBuses {

  lazy final val FIRE_CHANGE_SENDER: EventBus = new EventBus("fire-changes-sender") {
    private[this] val fireChangeRecorderRegistered = new AtomicBoolean(false)

    override def register(obj: Any): Unit = {
      if (obj.isInstanceOf[FireChangeRecorder]) {
        if (fireChangeRecorderRegistered.compareAndSet(false, true)) {
          super.register(obj)
        }
      } else {
        super.register(obj)
      }
    }
  }

  lazy final val FIRE_CHANGE_RECEIVER: EventBus = new EventBus("fire-changes-receiver")

}
