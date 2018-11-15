package org.jmotor.metral.rabbitmq

import java.util.UUID
import java.util.concurrent.CountDownLatch

import com.google.common.eventbus.{ EventBus, Subscribe }
import com.typesafe.config.ConfigFactory
import org.jmotor.metral.client.ExchangeType
import org.jmotor.metral.client.impl.{ RabbitConsumer, RabbitProducer }
import org.jmotor.metral.dto.{ FireChanged, Operation }
import org.scalatest.FunSuite

/**
 * Component:
 * Description:
 * Date: 2018/11/12
 *
 * @author AI
 */
class RabbitClientSpec extends FunSuite {

  test("send and consume") {

    val config = ConfigFactory.load()

    val producer = new RabbitProducer(config)
    val exchange = "fire-change"
    producer.declare(exchange, ExchangeType.DIRECT)

    val latch = new CountDownLatch(100)
    val consumer = new RabbitConsumer(config)
    val queue = "fire-change.metrics"
    consumer.bind(exchange, queue, "metrics", durable = true)

    val eb = new EventBus("RabbitClientSpec")
    consumer.subscribe(queue, eb)
    eb.register(new Recorder(latch))

    (1 to 100).foreach { id ⇒
      producer.send(exchange, "metrics", UUID.randomUUID().toString,
        FireChanged.newBuilder().setEntity("metrics").setIdentity(String.valueOf(id)).setOperation(Operation.CREATE).setTimestamp(System.currentTimeMillis())
          .build())
    }

    latch.await()
  }

  class Recorder(latch: CountDownLatch) {

    @Subscribe def handleFireChange(e: FireChanged): Unit = {
      println(e.toString)
      latch.countDown()
    }

  }

}
