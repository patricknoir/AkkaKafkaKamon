package org.patricknoir.kamon.akka.kafka.util

import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._

class AkkaKafkaKamonConfig(config: Config) {

  val refreshInterval: Duration = FiniteDuration(config.getDuration("kamon.akka.kafka.parseInterval").getSeconds, TimeUnit.SECONDS)

}

object AkkaKafkaKamonConfig {

  def apply() = new AkkaKafkaKamonConfig(ConfigFactory.load())

}
