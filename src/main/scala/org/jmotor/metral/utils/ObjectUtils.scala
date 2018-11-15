package org.jmotor.metral.utils

import java.util.Objects

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
object ObjectUtils {

  def toString(any: AnyRef): String = {
    any match {
      case s: String ⇒ s
      case v: AnyRef ⇒ Objects.toString(v)
    }
  }

}
