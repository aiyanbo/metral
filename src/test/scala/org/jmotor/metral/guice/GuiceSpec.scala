package org.jmotor.metral.guice

import java.util.concurrent.CountDownLatch

import com.google.common.eventbus.Subscribe
import com.google.inject.{ AbstractModule, Guice }
import org.jmotor.metral.MessageCentral
import org.jmotor.metral.dto.{ FireChanged, Operation }
import org.jmotor.metral.service.impl.NonaServiceImpl
import org.jmotor.metral.service.{ Nona, NonaService }
import org.scalatest.FunSuite

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Component:
 * Description:
 * Date: 2018/11/12
 *
 * @author AI
 */
class GuiceSpec extends FunSuite {

  test("method interceptor") {
    val name = "nona"
    val injector = Guice.createInjector(new AbstractModule {
      override def configure(): Unit = {
        bind(classOf[NonaService]).to(classOf[NonaServiceImpl])
      }
    }, new FireChangedModule)

    val metral = MessageCentral()

    val service = injector.getInstance(classOf[NonaService])

    val latch = new CountDownLatch(5)
    val recorder = new EventChangedRecorder(latch)

    metral.subscribeFireChange(name, recorder, global = true)

    val nona = Nona(-1, "create without id")
    Await.result(service.createWithoutId(nona), 10.seconds)
    Await.result(service.update(Nona(10, "update")), 10.seconds)
    Await.result(service.deleteById(11), 10.seconds)
    service.deleteSyncById(12)
    service.createSync(nona)

    latch.await()

    metral.shutdown()

    assert(recorder.events.size == 5)

    assert(recorder.events.exists { e ⇒
      e.getEntity == name && Operation.CREATE == e.getOperation && e.getIdentity == "1"
    })
    assert(recorder.events.exists { e ⇒
      e.getEntity == name && Operation.CREATE == e.getOperation && e.getIdentity == "13"
    })
    assert(recorder.events.exists { e ⇒
      e.getEntity == name && Operation.MODIFY == e.getOperation && e.getIdentity == "10"
    })
    assert(recorder.events.exists { e ⇒
      e.getEntity == name && Operation.DELETE == e.getOperation && e.getIdentity == "11"
    })
    assert(recorder.events.exists { e ⇒
      e.getEntity == name && Operation.DELETE == e.getOperation && e.getIdentity == "12"
    })
  }

  class EventChangedRecorder(latch: CountDownLatch) {

    val events: ListBuffer[FireChanged] = ListBuffer[FireChanged]()

    @Subscribe def log(e: FireChanged): Unit = {
      println(e.toString)
      events += e
      latch.countDown()
    }

  }

}
