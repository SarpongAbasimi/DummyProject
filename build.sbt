import sbtassembly.AssemblyPlugin.autoImport.assemblyMergeStrategy

lazy val http4sVersion                 = "0.21.21"
lazy val circeVersion                  = "0.12.3"
lazy val doobieVersion                 = "0.12.1"
lazy val pureConfigVersion             = "0.15.0"
lazy val catsEffectTestVersions        = "0.5.3"
lazy val logForCatVersion              = "1.1.1"
lazy val testContainersScalaVersion    = "0.39.1"
lazy val scalaCheckVersion             = "1.14.1"
lazy val scalaTestScalaCheckVersion    = "3.2.5.0"
lazy val circeGenericExtraVersion      = "0.13.0"
lazy val chrisDavenportLog4CatsVersion = "1.1.1"
lazy val circeLiteralVersion           = "0.13.0"
lazy val logbackVersion                = "1.2.3"
lazy val kafkaFs2Version               = "1.7.0"
lazy val fs2KafkaVulcan                = "1.7.0"
lazy val embeddedKafkaVersion          = "2.8.0"

lazy val applicationSettings = Seq(
  version := "0.1",
  scalaVersion := "2.13.5",
  libraryDependencies ++= Seq(
    "org.http4s"              %% "http4s-dsl"                      % http4sVersion,
    "org.http4s"              %% "http4s-blaze-server"             % http4sVersion,
    "org.http4s"              %% "http4s-blaze-client"             % http4sVersion,
    "org.http4s"              %% "http4s-circe"                    % http4sVersion,
    "com.github.pureconfig"   %% "pureconfig"                      % pureConfigVersion,
    "org.tpolecat"            %% "doobie-scalatest"                % doobieVersion              % Test,
    "com.codecommit"          %% "cats-effect-testing-scalatest"   % catsEffectTestVersions     % Test,
    "org.http4s"              %% "http4s-prometheus-metrics"       % http4sVersion,
    "io.circe"                %% "circe-generic-extras"            % circeGenericExtraVersion,
    "io.chrisdavenport"       %% "log4cats-slf4j"                  % chrisDavenportLog4CatsVersion,
    "io.circe"                %% "circe-literal"                   % circeLiteralVersion,
    "ch.qos.logback"           % "logback-classic"                 % logbackVersion,
    "org.tpolecat"            %% "doobie-core"                     % doobieVersion,
    "org.tpolecat"            %% "doobie-postgres"                 % doobieVersion,
    "org.tpolecat"            %% "doobie-specs2"                   % doobieVersion,
    "com.dimafeng"            %% "testcontainers-scala-postgresql" % testContainersScalaVersion % "test",
    "com.dimafeng"            %% "testcontainers-scala-scalatest"  % testContainersScalaVersion % "test",
    "org.scalacheck"          %% "scalacheck"                      % scalaCheckVersion          % "test",
    "org.scalatestplus"       %% "scalacheck-1-15"                 % scalaTestScalaCheckVersion % "test",
    "com.github.fd4s"         %% "fs2-kafka"                       % kafkaFs2Version,
    "com.github.fd4s"         %% "fs2-kafka-vulcan"                % fs2KafkaVulcan,
    "io.github.embeddedkafka" %% "embedded-kafka"                  % embeddedKafkaVersion       % Test
  ),
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion),
  libraryDependencies ++= Seq(
    "org.flywaydb"   % "flyway-maven-plugin" % "7.8.2",
    "com.h2database" % "h2"                  % "1.4.197"
  ),
  assemblyMergeStrategy in assembly := {
    case "application.conf" => MergeStrategy.concat
    case PathList("META-INF", "MANIFEST.MF") =>
      MergeStrategy.discard
    case _ => MergeStrategy.first
  },
  resolvers ++= Seq(
    "confluent" at "https://packages.confluent.io/maven/"
  )
)

lazy val persistenceService = (project in file("modules/persistenceService"))
  .settings(
    moduleName := "persistenceService",
    Test / fork := true,
    applicationSettings,
    assembly / assemblyJarName := "persistenceService_2.13-0.1.jar"
  )
  .dependsOn(
    algebrasAndModel
  )

lazy val algebrasAndModel = (project in file("modules/algebrasAndModel"))
  .settings(
    moduleName := "algebrasAndModel",
    applicationSettings,
    assembly / assemblyJarName := "algebrasAndModel_2.13-0.1.jar"
  )

lazy val subscriptionService = (project in file("modules/subscriptionService"))
  .settings(
    moduleName := "subscriptionServiceAlgebra",
    applicationSettings,
    assembly / assemblyJarName := "subscriptionService-assembly-0.1.jar"
  )
  .dependsOn(
    algebrasAndModel,
    persistenceService
  )

lazy val root = (project in file("."))
  .settings(
    name := "dummyProject",
    assembly / assemblyJarName := "dummyProject-assembly-0.1.jar",
    applicationSettings
  )
  .dependsOn(
    persistenceService,
    algebrasAndModel,
    subscriptionService
  )
  .aggregate(
    persistenceService,
    algebrasAndModel,
    subscriptionService
  )
