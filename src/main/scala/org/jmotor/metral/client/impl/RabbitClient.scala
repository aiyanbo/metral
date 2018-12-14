package org.jmotor.metral.client.impl

import java.io.Closeable
import java.util.Objects

import com.rabbitmq.client.{ Address, Channel, Connection, ConnectionFactory }
import com.typesafe.config.Config

import scala.collection.JavaConverters._
import scala.util.{ Failure, Success, Try }

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
class RabbitClient(config: Config) extends Closeable {

  private[this] val lock: Object = new Object
  private[this] val rabbitConfig: Config = config.getConfig("metral.rabbit")
  private[this] val factory = buildConnectionFactory(rabbitConfig)
  private[this] var channel: Channel = _
  private[this] var connection: Connection = _

  private[impl] def buildConnectionFactory(config: Config): ConnectionFactory = {
    val factory = new ConnectionFactory()
    factory.setHost(config.getString("host"))
    factory.setUsername(config.getString("username"))
    factory.setPassword(config.getString("password"))
    factory.setVirtualHost(config.getString("virtual-host"))
    factory
  }

  protected def getOrCreateChannel: Channel = {
    if (Objects.isNull(channel) || !channel.isOpen) {
      lock.synchronized {
        if (Objects.isNull(channel) || !channel.isOpen) {
          channel = getOrCreateConnection.createChannel()
        }
      }
    }
    channel
  }

  private[impl] def getOrCreateConnection: Connection = {
    if (Objects.isNull(connection) || !connection.isOpen) {
      val port = rabbitConfig.getInt("port")
      Try(rabbitConfig.getStringList("hosts")) match {
        case Success(hosts) ⇒
          val addrs = hosts.asScala.map(host ⇒ new Address(host, port)).asJava
          connection = factory.newConnection(addrs)
        case Failure(_) ⇒
          factory.setPort(port)
          connection = factory.newConnection()
      }
    }
    connection
  }

  override def close(): Unit = {
    Try(channel.close())
    Try(connection.close())
  }

}
