package org.jmotor.metral.client.impl

import java.io.Closeable
import java.util.Objects

import com.rabbitmq.client.{ Channel, Connection, ConnectionFactory }
import com.typesafe.config.Config

import scala.util.Try

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
class RabbitClient(config: Config) extends Closeable {

  private[this] val lock: Object = new Object
  private[this] val factory = buildConnectionFactory(config.getConfig("metral.rabbit"))
  private[this] var channel: Channel = _
  private[this] var connection: Connection = _

  private[impl] def buildConnectionFactory(config: Config): ConnectionFactory = {
    val factory = new ConnectionFactory()
    factory.setPort(config.getInt("port"))
    factory.setHost(config.getString("host"))
    factory.setUsername(config.getString("username"))
    factory.setPassword(config.getString("password"))
    factory.setVirtualHost(config.getString("virtual-host"))
    factory
  }

  protected def getChannel: Channel = {
    if (Objects.isNull(channel) || !channel.isOpen) {
      lock.synchronized {
        if (Objects.isNull(channel) || !channel.isOpen) {
          channel = getConnection.createChannel()
        }
      }
    }
    channel
  }

  private[impl] def getConnection: Connection = {
    if (Objects.isNull(connection) || !connection.isOpen) {
      connection = factory.newConnection()
    }
    connection
  }

  override def close(): Unit = {
    Try(channel.close())
    Try(connection.close())
  }

}
