package org.jmotor.metral.client

import java.io.Closeable

import com.google.common.eventbus.EventBus
import org.jmotor.metral.api.MessageHandler

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
trait Consumer extends Closeable {

  def subscribe(queue: String, eventBus: EventBus)

  def subscribe(queue: String, handler: MessageHandler)

  def bind(exchange: String, queue: String, routing: String, durable: Boolean)

}
