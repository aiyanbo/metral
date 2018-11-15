package org.jmotor.metral.client

import java.io.Closeable

import com.google.protobuf.AbstractMessage
import org.jmotor.metral.client.ExchangeType.ExchangeType

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
trait Producer extends Closeable {

  def declare(exchange: String, typ: ExchangeType)

  def send(exchange: String, key: String, message: AbstractMessage)

  def send(exchange: String, routing: String, key: String, message: AbstractMessage)

}
