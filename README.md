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
