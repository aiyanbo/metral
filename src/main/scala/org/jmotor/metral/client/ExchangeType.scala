package org.jmotor.metral.client

/**
 * Component:
 * Description:
 * Date: 2018/11/15
 *
 * @author AI
 */
object ExchangeType extends Enumeration {

  type ExchangeType = Value

  val DIRECT: Value = Value("DIRECT")

  val FANOUT: Value = Value("FANOUT")

}
