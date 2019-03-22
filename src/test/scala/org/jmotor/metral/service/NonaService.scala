package org.jmotor.metral.service

import com.google.common.primitives.Longs
import org.jmotor.metral.event.{ EventSource, EventSourceBuilder }

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

class LongIdEventSourceBuilder extends EventSourceBuilder {

  override def build(arguments: Array[AnyRef], returnObject: Any): EventSource = {
    EventSource.build(Longs.toByteArray(arguments.head.asInstanceOf[Long]))
  }

}

class NonaUpdateEventSourceBuilder extends EventSourceBuilder {
  override def build(arguments: Array[AnyRef], returnObject: Any): EventSource = {
    EventSource.build(Longs.toByteArray(arguments.head.asInstanceOf[Nona].id))
  }
}

class LongIdReturnObjectEventSourceBuilder extends EventSourceBuilder {

  override def build(arguments: Array[AnyRef], returnObject: Any): EventSource = {
    EventSource.build(Longs.toByteArray(returnObject.asInstanceOf[Long]))
  }

}

