package org.patricknoir.kamon.akka.kafka


import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.testkit.TestKit
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, Deserializer, StringDeserializer}
import org.patricknoir.kamon.akka.kafka.util.AkkaKafkaKamonConfig
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncWordSpecLike, BeforeAndAfterAll, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class End2End extends TestKit(ActorSystem("end2end"))
  with AsyncWordSpecLike
  with Matchers
  with AsyncMockFactory
  with BeforeAndAfterAll
  with EmbeddedKafka {

  implicit val mat: ActorMaterializer = ActorMaterializer()
  private val kafkaPort = 9092
  private val bootstrapServer = s"localhost:$kafkaPort"
  private implicit val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = kafkaPort)
  private implicit val serializer: ByteArraySerializer = new ByteArraySerializer
  private implicit val deserializer: ByteArrayDeserializer = new ByteArrayDeserializer
  private val outputTopicNotifications = "outputTopicNotifications"
  private val outputTopicEvents = "outputTopicEvents"
  private val inputTopic = "inputTopic"


  private val kafkaTimeout = 50 seconds

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "AkkaKafkaKamon" should {

    "monitor the Consumer of a running stream" in {

      val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
        .withBootstrapServers("localhost:9092")
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")


      val source: Source[ConsumerRecord[Array[Byte], String], Consumer.Control] = Consumer.plainSource(consumerSettings, Subscriptions.topics("test-topic"))

      val control = source.toMat(Sink.ignore)(Keep.left).run()

      val monitoring = new ConsumerPartitionsCollector(control, AkkaKafkaKamonConfig())

      val f = monitoring.start()

      Await.ready(f, Duration.Inf)

      "monitoring" shouldBe "monitoring"
    }
  }


}