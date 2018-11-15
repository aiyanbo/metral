import sbt._

object Dependencies {

  object Versions {
    val guice = "4.2.2"
    val config = "1.3.3"
    val scala212 = "2.12.7"
    val scalatest = "3.0.5"
    val guava = "27.0-jre"
    val scala211 = "2.11.11"
    val scala210 = "2.10.7"
    val amqpClient = "5.5.0"
  }

  object Compiles {
    val config = "com.typesafe" % "config" % Versions.config
    val guava = "com.google.guava" % "guava" % Versions.guava
    val amqpClient = "com.rabbitmq" % "amqp-client" % Versions.amqpClient
    val guice = "com.google.inject" % "guice" % Versions.guice exclude("com.google.guava", "guava")
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = Seq(config, guice, guava, amqpClient, Tests.scalaTest)

}
