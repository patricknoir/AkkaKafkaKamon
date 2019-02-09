# AkkaKafkaKamon

## Introduction

This is a simple library which given an Akka Stream using Akka Kafka sources, 
is able to pull topic/partition metrics from the ConsumerControl.

```scala
val consumerControl = kafkaSource.toMat(Sink.ignore)(Keep.left).run()

val monitor = ConsumerPartitionsCollector(consumerControl, AkkaKafkaConfig())

val futureDone = monitor.start()(ExecutionContext.global)

```

This library will  extract the metrics under the group: `consumer-fetch-manager-metrics`, for each consumer it will collect the dynamic attributes associated to the topic-partition is consuming:

```
<topic name>-<partition number>.records-lag
...
```


Metrics are exposed as histograms for each partition the client is consuming:

```
# TYPE kafka_consumer_partition_records_lag histogram
kafka_consumer_partition_records_lag_bucket{le="10.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="30.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="100.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="300.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="1000.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="3000.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="10000.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="30000.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="100000.0",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_bucket{le="+Inf",partition="0",metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName"} 8.0
kafka_consumer_partition_records_lag_count{metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName",partition="0"} 8.0
kafka_consumer_partition_records_lag_sum{metric_group="consumer-fetch-manager-metrics",consumer_name="consumer-1",topic="topicName",partition="0"} 0.0
```
