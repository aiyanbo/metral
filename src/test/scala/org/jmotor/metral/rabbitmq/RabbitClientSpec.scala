package org.jmotor.metral.rabbitmq

import java.util.UUID
import java.util.concurrent.CountDownLatch

import com.google.common.eventbus.{ EventBus, Subscribe }
import com.google.common.primitives.{ Ints, Longs }
import com.google.protobuf.ByteString
import com.typesafe.config.ConfigFactory
import org.jmotor.metral.api.{ Acknowledge, MessageHandler }
import org.jmotor.metral.client.ExchangeType
import org.jmotor.metral.client.impl.{ RabbitConsumer, RabbitProducer }
import org.jmotor.metral.dto.{ FireChanged, Message, Operation }
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
        FireChanged.newBuilder().setEntity("metrics")
          .setIdentity(ByteString.copyFrom(Ints.toByteArray(id)))
          .setOperation(Operation.CREATE).setTimestamp(System.currentTimeMillis())
          .build())
    }

    latch.await()

    producer.close()
    consumer.close()

  }

  test("send and subscribe") {

    val config = ConfigFactory.load()

    val producer = new RabbitProducer(config)
    val exchange = "global.jobs"
    producer.declare(exchange, ExchangeType.DIRECT)

    val latch = new CountDownLatch(100)
    val consumer = new RabbitConsumer(config)
    val topic = "download-job"
    val queue = "global.jobs." + topic
    consumer.bind(exchange, queue, topic, durable = true)
    consumer.subscribe(queue, new MessageHandler {
      override def handle(message: Message, ack: Acknowledge): Unit = {
        assert(message.getTopic == topic)
        ack.ack()
        latch.countDown()
      }
    })

    (1 to 100).foreach { id ⇒
      producer.send(exchange, topic, UUID.randomUUID().toString,
        Message.newBuilder().setTopic(topic).build())
    }

    latch.await()

    producer.close()
    consumer.close()

  }

  class Recorder(latch: CountDownLatch) {

    @Subscribe def handleFireChange(e: FireChanged): Unit = {
      println(e.toString)
      latch.countDown()
    }

  }

}
