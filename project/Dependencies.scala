import sbt._

object Dependencies {

  object Versions {
    val log4j2 = "11.0"
    val guice = "4.2.2"
    val config = "1.3.4"
    val guava = "28.0-jre"
    val scala210 = "2.10.7"
    val scala213 = "2.13.0"
    val scalatest = "3.0.8"
    val scala212 = "2.12.8"
    val scala211 = "2.11.12"
    val amqpClient = "5.7.2"
    val scalaLogging = "3.9.2"
  }

  object Compiles {
    val config = "com.typesafe" % "config" % Versions.config
    val guava = "com.google.guava" % "guava" % Versions.guava
    val amqpClient = "com.rabbitmq" % "amqp-client" % Versions.amqpClient
    val logging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
    val guice = "com.google.inject" % "guice" % Versions.guice exclude ("com.google.guava", "guava")
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = Seq(config, guice, guava, logging, amqpClient, Tests.scalaTest)

}
