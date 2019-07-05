package org.jmotor.metral.utils

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success, Try }

/**
 * Component:
 * Description:
 * Date: 2018/11/20
 *
 * @author AI
 */
object Retryable extends LazyLogging {

  def retry[T](execution: () ⇒ T)(attempts: Int): T = {
    Try(execution()) match {
      case Success(r)                 ⇒ r
      case Failure(_) if attempts > 0 ⇒ retry(execution)(attempts - 1)
      case Failure(t)                 ⇒ throw t
    }
  }

  def retryDuration[T](execution: () ⇒ T)(duration: Duration, attempts: Int): T = {
    Try(execution()) match {
      case Success(r) ⇒ r
      case Failure(t) if attempts > 0 ⇒
        logger.error(t.getLocalizedMessage, t)
        Thread.sleep(duration.toMillis)
        retryDuration(execution)(duration, attempts - 1)
      case Failure(t) ⇒ throw t
    }
  }

}
