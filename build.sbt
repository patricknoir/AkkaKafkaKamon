name := "AkkaKafkaKamon"
organization := "org.patricknoir"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.12.8"

val Versions = new {
  val AkkaStreamKafka = "0.22"
  val KamonCore = "1.1.+"
  val Akka = "2.5.+"
  val Cats = "1.6.0"
}

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "io.kamon"          %% "kamon-core"        % Versions.KamonCore,
  "com.typesafe.akka" %% "akka-stream-kafka" % Versions.AkkaStreamKafka,
  "com.typesafe.akka" %% "akka-actor"        % Versions.Akka,
  "org.typelevel"     %% "cats-core"         % Versions.Cats
)