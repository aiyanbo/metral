import sbt._

object Dependencies {

  object Versions {
    val guice = "4.2.2"
    val log4j2 = "11.0"
    val config = "1.4.0"
    val guava = "28.1-jre"
    val scalatest = "3.0.8"
    val scala212 = "2.12.10"
    val amqpClient = "5.7.3"
    val scala211 = "2.11.12"
    val scalaLogging = "3.9.2"
    val protobufJava = "3.10.0"
    val scalaLibrary = "2.13.0"
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
