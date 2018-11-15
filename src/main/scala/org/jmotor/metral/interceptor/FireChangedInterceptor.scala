package org.jmotor.metral.interceptor

import java.util.concurrent.{ LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit }

import com.google.common.eventbus.EventBus
import org.aopalliance.intercept.{ MethodInterceptor, MethodInvocation }
import org.jmotor.metral.annotaion.FireChanged
import org.jmotor.metral.dto.Operation
import org.jmotor.metral.utils.ObjectUtils

import scala.concurrent.{ ExecutionContext, ExecutionContextExecutor, Future }
import scala.language.implicitConversions

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
class FireChangedInterceptor(bus: EventBus) extends MethodInterceptor {
  private[this] lazy final val MaxFireChangeQueueSize = 1000
  private[this] lazy implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(
    new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable](MaxFireChangeQueueSize)))

  override def invoke(invocation: MethodInvocation): AnyRef = {
    val result = invocation.proceed()
    caught(invocation, result)
    result
  }

  private[interceptor] def caught(invocation: MethodInvocation, result: AnyRef): Unit = {
    result match {
      case future: Future[AnyRef] @unchecked ⇒ future.foreach(obj ⇒ fireChange(invocation, obj))
      case obj: Any                          ⇒ fireChange(invocation, obj)
      case _                                 ⇒ fireChange(invocation, null)
    }
  }

  private[interceptor] def fireChange(invocation: MethodInvocation, obj: AnyRef): Unit = {
    val annotation = invocation.getMethod.getAnnotation(classOf[FireChanged])
    val parameterIndex = annotation.identityParameterIndex()
    val identity: String = if (parameterIndex > -1) {
      ObjectUtils.toString(invocation.getArguments()(parameterIndex))
    } else {
      annotation.operation() match {
        case Operation.CREATE ⇒ annotation.returnTranslator().newInstance().translate(Array(obj))
        case _                ⇒ annotation.parameterTranslator().newInstance().translate(invocation.getArguments)
      }
    }
    val fireChanged = org.jmotor.metral.dto.FireChanged.newBuilder().setEntity(annotation.entity())
      .setIdentity(identity).setOperation(annotation.operation()).setTimestamp(System.currentTimeMillis()).build()
    bus.post(fireChanged)
  }

}
