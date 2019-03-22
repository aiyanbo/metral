package org.jmotor.metral.interceptor

import java.time.Duration
import java.util.Map.Entry
import java.util.Objects
import java.util.concurrent.{ Callable, LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit }
import java.util.function.Consumer

import com.google.common.cache.{ Cache, CacheBuilder }
import com.google.common.eventbus.EventBus
import com.google.protobuf.ByteString
import org.aopalliance.intercept.{ MethodInterceptor, MethodInvocation }
import org.jmotor.metral.annotaion.FireChanged
import org.jmotor.metral.event.EventSourceBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
class FireChangedInterceptor(bus: EventBus) extends MethodInterceptor {
  private[this] lazy final val maxBuilderSize = 1000
  private[this] lazy final val maxBuilderTtlOfMinutes = 30
  private[this] lazy final val maxFireChangeQueueSize = 1000
  private[this] lazy final val maxBuilderTTL = Duration.ofMinutes(maxBuilderTtlOfMinutes)
  private[this] lazy final val builders: Cache[Class[_], EventSourceBuilder] = CacheBuilder.newBuilder()
    .maximumSize(maxBuilderSize)
    .expireAfterAccess(maxBuilderTTL)
    .build()
  private[this] lazy final val executor = new ThreadPoolExecutor(
    1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable](maxFireChangeQueueSize))

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
    executor.execute(new Runnable {
      override def run(): Unit = {
        val annotation = invocation.getMethod.getAnnotation(classOf[FireChanged])
        val builderClass = annotation.builder()
        val builder = builders.get(builderClass, new Callable[EventSourceBuilder] {
          override def call(): EventSourceBuilder = {
            builderClass.newInstance()
          }
        })
        val source = builder.build(invocation.getArguments, obj)
        val eventBuilder = org.jmotor.metral.dto.FireChanged.newBuilder()
          .setEntity(annotation.entity())
          .setIdentity(ByteString.copyFrom(source.getIdentity))
          .setOperation(annotation.operation())
          .setTimestamp(System.currentTimeMillis())
        if (Objects.nonNull(source.getAttributes) && !source.getAttributes.isEmpty) {
          source.getAttributes.entrySet().forEach(new Consumer[Entry[String, Array[Byte]]] {
            override def accept(t: Entry[String, Array[Byte]]): Unit = {
              eventBuilder.putAttributes(t.getKey, ByteString.copyFrom(t.getValue))
            }
          })
        }
        bus.post(eventBuilder.build())
      }

    })
  }
}
