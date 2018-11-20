package org.jmotor.metral.utils

import org.scalatest.FunSuite

import scala.util.control.NonFatal

/**
 * Component:
 * Description:
 * Date: 2018/11/20
 *
 * @author AI
 */
class RetryableSpec extends FunSuite {

  test("retry 3") {
    var count = 0
    val maxAttempts = 3
    try {
      Retryable.retry(() ⇒ {
        count += 1
        throw new NullPointerException
      })(maxAttempts)
    } catch {
      case NonFatal(t) ⇒
        assert(count == 4)
        assert(t.isInstanceOf[NullPointerException])
    }
  }

}
