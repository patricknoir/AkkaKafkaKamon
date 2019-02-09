package org.patricknoir.kamon.akka.kafka.model

case class KafkaPartitionMetric(
  metricName: String,
  metricGroup: String,
  consumerName: String,
  topic: String,
  partition: Int
) {
  val fullMatricName = "kafka_consumer_partition_" + metricName

  val tags = ("metric_group" -> metricGroup) :: ("consumer_name" -> consumerName) :: ("topic" -> topic) :: ("partition" -> partition.toString) :: Nil
}


case class KafkaPartitionMetricValue(
  partitionMetric: KafkaPartitionMetric,
  value: Long
)

