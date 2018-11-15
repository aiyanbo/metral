package org.jmotor.metral.service.impl

import javax.inject.Singleton
import org.jmotor.metral.annotaion.FireChanged
import org.jmotor.metral.dto.Operation
import org.jmotor.metral.service.{ Nona, NonaService, NonaUpdateTranslator }

import scala.concurrent.Future

/**
 * Component:
 * Description:
 * Date: 2018/11/12
 *
 * @author AI
 */
@Singleton
class NonaServiceImpl extends NonaService {

  @FireChanged(entity = K.n, operation = Operation.CREATE)
  override def createWithoutId(nano: Nona): Future[Long] = Future.successful(1L)

  @FireChanged(entity = "nona", operation = Operation.MODIFY, parameterTranslator = classOf[NonaUpdateTranslator])
  override def update(nano: Nona): Future[Int] = Future.successful(1)

  @FireChanged(entity = "nona", operation = Operation.DELETE, identityParameterIndex = 0)
  override def deleteById(id: Long): Future[Boolean] = Future.successful(true)

  @FireChanged(entity = "nona", operation = Operation.DELETE, identityParameterIndex = 0)
  override def deleteSyncById(id: Long): Unit = ()
}

object K {

  final val n = "nona"

}
