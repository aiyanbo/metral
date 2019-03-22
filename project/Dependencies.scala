import sbt._

object Dependencies {

  object Versions {
    val log4j2 = "11.0"
    val guice = "4.2.2"
    val config = "1.3.3"
    val guava = "27.1-jre"
    val scala210 = "2.10.7"
    val scalatest = "3.0.7"
    val scala212 = "2.12.8"
    val scala211 = "2.11.11"
    val amqpClient = "5.6.0"
  }

  object Compiles {
    val config = "com.typesafe" % "config" % Versions.config
    val guava = "com.google.guava" % "guava" % Versions.guava
    val amqpClient = "com.rabbitmq" % "amqp-client" % Versions.amqpClient
    val log4j2 = "org.apache.logging.log4j" %% "log4j-api-scala" % Versions.log4j2
    val guice = "com.google.inject" % "guice" % Versions.guice exclude ("com.google.guava", "guava")
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = Seq(config, guice, guava, log4j2, amqpClient, Tests.scalaTest)

}
