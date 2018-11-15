package org.jmotor.metral.service

import org.jmotor.metral.translator.IdentityTranslator

import scala.concurrent.Future

/**
 * Component:
 * Description:
 * Date: 2018/11/12
 *
 * @author AI
 */
trait NonaService {

  def createWithoutId(nano: Nona): Future[Long]

  def update(nano: Nona): Future[Int]

  def deleteById(id: Long): Future[Boolean]

  def deleteSyncById(id: Long): Unit

  def createSync(nano: Nona): Long

}

class NonaUpdateTranslator extends IdentityTranslator {

  override def translate(args: Array[AnyRef]): String = args(0).asInstanceOf[Nona].id.toString

}
