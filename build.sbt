
name := "AkkaKafkaKamon"
organization := "org.patricknoir"
version := "0.1.0"
scalaVersion := "2.12.8"

val Versions = new {
  val AkkaStreamKafka = "0.22"
  val KamonCore = "1.1.+"
  val Akka = "2.5.+"
  val Cats = "1.6.0"
}

val TestVersions = new {
  val ScalaTest = "3.0.4"
  val ScalaCheck = "1.14.0"
  val AkkaTest = Versions.Akka
  val Mockito = "2.13.0"
  val WireMock = "2.16.0"
  val KafkaEmbedded = "1.1.0"
  val RandomDataGenerator = "2.5"
  val ScalaMock = "4.1.0"
  val PersistenceInmemory = "2.5.1.1"
}

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "io.kamon"          %% "kamon-core"        % Versions.KamonCore,
  "com.typesafe.akka" %% "akka-stream-kafka" % Versions.AkkaStreamKafka,
  "com.typesafe.akka" %% "akka-actor"        % Versions.Akka,
  "org.typelevel"     %% "cats-core"         % Versions.Cats,

  "org.scalatest" %% "scalatest" % TestVersions.ScalaTest % "test",
  "org.mockito" % "mockito-core" % TestVersions.Mockito % "test",
  "com.typesafe.akka" %% "akka-testkit" % TestVersions.AkkaTest % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % TestVersions.AkkaTest % "test",
  "com.github.tomakehurst" % "wiremock" % TestVersions.WireMock % "test",
  "net.manub" %% "scalatest-embedded-kafka" % TestVersions.KafkaEmbedded % "test",
  "org.scalacheck" %% "scalacheck" % TestVersions.ScalaCheck % "test",
  "com.danielasfregola" %% "random-data-generator" % TestVersions.RandomDataGenerator % "test",
  "org.scalamock" %% "scalamock" % TestVersions.ScalaMock % "test",
  "com.typesafe.akka" %% "akka-stream" % Versions.Akka % "test"
)
