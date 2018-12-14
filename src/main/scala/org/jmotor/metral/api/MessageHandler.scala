package org.jmotor.metral.api

import org.jmotor.metral.dto.Message

/**
 * Component:
 * Description:
 * Date: 2018-12-11
 *
 * @author AI
 */
trait MessageHandler {

  def handle(message: Message, ack: Acknowledge): Unit

}
