package org.jmotor.metral.internal

import java.util.concurrent.CountDownLatch

import com.typesafe.config.ConfigFactory
import org.jmotor.metral.api.{ Acknowledge, Exchange, MessageHandler }
import org.jmotor.metral.client.ExchangeType
import org.jmotor.metral.{ MessageCentral, SubscribePolicy }
import org.jmotor.metral.dto.Message
import org.scalatest.FunSuite

/**
 * Component:
 * Description:
 * Date: 2018-12-29
 *
 * @author AI
 */
class MessageCentralSpec extends FunSuite {

  test("subscribe") {
    val mc = MessageCentral(ConfigFactory.load())
    val count = 100
    val latch = new CountDownLatch(count)

    val topic = "download-job"
    val exchange = "global.jobs"

    mc.declare(Exchange(exchange, ExchangeType.DIRECT))

    mc.subscribe(exchange, topic, SubscribePolicy.GLOBAL, new MessageHandler {
      override def handle(message: Message, ack: Acknowledge): Unit = {
        assert(message.getTopic == topic)
        ack.ack()
        latch.countDown()
      }
    })

    (0 to count).foreach { _ ⇒
      mc.send(exchange, Message.newBuilder().setTopic(topic).build())
    }

    latch.await()

    mc.shutdown()

  }

}
