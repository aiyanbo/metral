package org.jmotor.metral.utils

import org.apache.logging.log4j.scala.Logging

import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success, Try }

/**
 * Component:
 * Description:
 * Date: 2018/11/20
 *
 * @author AI
 */
object Retryable extends Logging {

  def retry[T](execution: () ⇒ T)(attempts: Int): T = {
    Try(execution()) match {
      case Success(r)                 ⇒ r
      case Failure(_) if attempts > 0 ⇒ retry(execution)(attempts - 1)
      case Failure(t)                 ⇒ throw t
    }
  }

  def retryDuration[T](execution: () ⇒ T)(duration: Duration): T = {
    Try(execution()) match {
      case Success(r) ⇒ r
      case Failure(t) ⇒
        logger.error(t.getLocalizedMessage, t)
        Thread.sleep(duration.toMillis)
        retryDuration(execution)(duration)
    }
  }

}
