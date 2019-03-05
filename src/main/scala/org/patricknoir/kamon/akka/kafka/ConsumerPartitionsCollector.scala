package org.patricknoir.kamon.akka.kafka

import java.util.concurrent.{ScheduledFuture, TimeUnit}
import java.util.concurrent.atomic.AtomicBoolean

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import kamon.Kamon
import org.apache.kafka.common.{Metric, MetricName}
import org.patricknoir.kamon.akka.kafka.model.{KafkaPartitionMetric, KafkaPartitionMetricValue}
import org.patricknoir.kamon.akka.kafka.util.{AkkaKafkaKamonConfig, KafkaConstants}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Success, Try}
import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration

/**
  * This is the class in charge to collect all the metrics related only to the topic partition performances.
  *
  * The only metric to collect is the {topic}-{partition}.records-lag
  *
  * This is a dynamic attribute created for each partition.
  *
  * @param consumerControl
  * @param system
  */
class ConsumerPartitionsCollector(consumerControl: Consumer.Control, config: AkkaKafkaKamonConfig)(implicit system: ActorSystem) {

  private var optCancellable: Option[ScheduledFuture[_]] = None

  private def retrievePartitionRecordsLagMetrics()(implicit ec: ExecutionContext): Future[List[KafkaPartitionMetricValue]] = {
      for {
        metrics <-  consumerControl.metrics.map(filterRelevant)
      } yield metrics.map(metricInfo => convertKafkaPartitionMetricValue(metricInfo._1, metricInfo._2)).toList.collect { case Success(value) => value }
    }


  private def filterRelevant(metricsMap: Map[MetricName, Metric]): Map[MetricName, Metric] =
    metricsMap.filterKeys(metricName =>
      (metricName.group() == KafkaConstants.CONSUMER_FETCH_MANAGER_METRICS_GROUP) &&
        (
          (metricName.name.trim.endsWith(KafkaConstants.CONSUMER_PARTITION_RECORDS_LAG_NAME_SUFFIX)) ||
            (metricName.name.trim.endsWith(KafkaConstants.CONSUMER_PARTITION_RECORDS_LAG_AVG_NAME_SUFFIX)) ||
            (metricName.name.trim.endsWith(KafkaConstants.CONSUMER_PARTITION_RECORDS_LAG_MAX_NAME_SUFFIX))
        )
    )

  private def convertKafkaPartitionMetricValue(metricName: MetricName, metric: Metric): Try[KafkaPartitionMetricValue] =
    parseMetricName(metricName).map(partitionMetric => KafkaPartitionMetricValue(partitionMetric, metric.value().toLong))

  private def parseMetricName(metricName: MetricName): Try[KafkaPartitionMetric] = Try {
    val metricNameParts = metricName.name().split("\\.")
    val (topicName: String, partitionStr: String) = metricNameParts(0).splitAt(metricNameParts(0).lastIndexOf("-"))

    val name = metricNameParts(1).replace('-','_')

    KafkaPartitionMetric(
      metricName = name,
      metricGroup = metricName.group(),
      consumerName = metricName.tags().get("client-id"),
      topic = topicName,
      partition = partitionStr.toInt
    )
  }

  def start()(implicit ec: ExecutionContext): Option[ScheduledFuture[_]] = {
    optCancellable = Some(
      Kamon.scheduler().scheduleAtFixedRate(
        new Runnable {
          override def run(): Unit = {
            val fFilteredMetrics = retrievePartitionRecordsLagMetrics()
            fFilteredMetrics.foreach { filteredMetrics =>
              filteredMetrics.foreach(registerKamonMetric)
            }
          }
        },
        1L,
        config.refreshInterval.toSeconds,
        TimeUnit.SECONDS
      )
    )
    optCancellable
  }

  def stop() = {
    optCancellable.foreach(_.cancel(false))
    optCancellable = None
  }

  private def registerKamonMetric(metricValue: KafkaPartitionMetricValue): Unit = if (metricValue.value >= 0L){
    Kamon.gauge(metricValue.partitionMetric.fullMatricName).refine(
      metricValue.partitionMetric.tags:_*
    ).set(metricValue.value)
  }
}
