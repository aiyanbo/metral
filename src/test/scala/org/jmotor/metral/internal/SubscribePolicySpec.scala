package org.jmotor.metral.internal

import java.net.InetAddress

import com.typesafe.config.ConfigFactory
import org.jmotor.metral.SubscribePolicy
import org.scalatest.FunSuite

/**
 * Component:
 * Description:
 * Date: 2018-11-27
 *
 * @author AI
 */
class SubscribePolicySpec extends FunSuite {

  test("test get queue name") {

    val entity = "nona"
    val hostname = InetAddress.getLocalHost.getHostName

    val mc = new DefaultMessageCentral(ConfigFactory.load())

    assert(s"ha.metral.fire-changes.$entity" == mc.getFireChangeQueueName(entity, SubscribePolicy.GLOBAL))
    assert(s"metral.$hostname.fire-changes.$entity" == mc.getFireChangeQueueName(entity, SubscribePolicy.INSTANCE))
    assert(s"ha.metral.$hostname.fire-changes.$entity" == mc.getFireChangeQueueName(entity, SubscribePolicy.INSTANCE_DURABLE))

  }

}
