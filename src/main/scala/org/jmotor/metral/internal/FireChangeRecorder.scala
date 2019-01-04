package org.jmotor.metral.internal

import java.util.UUID

import com.google.common.eventbus.Subscribe
import org.jmotor.metral.client.Producer
import org.jmotor.metral.dto.FireChanged
import org.jmotor.metral.utils.Retryable

/**
 * Component:
 * Description:
 * Date: 2019-01-04
 *
 * @author AI
 */
private[internal] class FireChangeRecorder(exchange: String, producer: Producer) {
  private[this] lazy val maxAttempts = 100

  @Subscribe def handleFireChange(e: FireChanged): Unit = {
    Retryable.retry(() ⇒ producer.send(exchange, e.getEntity, UUID.randomUUID().toString, e))(maxAttempts)
  }

}
