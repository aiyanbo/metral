package org.jmotor.metral.guice

import com.google.inject.AbstractModule
import com.google.inject.matcher.Matchers
import org.jmotor.metral.annotaion.FireChanged
import org.jmotor.metral.interceptor.FireChangedInterceptor
import org.jmotor.metral.internal.EventBuses

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
class FireChangedModule extends AbstractModule {

  override def configure(): Unit = {
    val bus = EventBuses.FIRE_CHANGE_SENDER
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(classOf[FireChanged]), new FireChangedInterceptor(bus))
  }

}
