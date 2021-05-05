lazy val http4sVersion              = "0.21.21"
lazy val circeVersion               = "0.12.3"
lazy val doobieVersion              = "0.12.1"
lazy val pureConfigVersion          = "0.15.0"
lazy val catsEffectTestVersions     = "0.5.3"
lazy val logForCatVersion           = "1.1.1"
lazy val testContainersScalaVersion = "0.39.1"

lazy val applicationSettings = Seq(
  version := "0.1",
  scalaVersion := "2.13.5",
  libraryDependencies ++= Seq(
    "org.http4s"            %% "http4s-dsl"                    % http4sVersion,
    "org.http4s"            %% "http4s-blaze-server"           % http4sVersion,
    "org.http4s"            %% "http4s-blaze-client"           % http4sVersion,
    "org.http4s"            %% "http4s-circe"                  % http4sVersion,
    "com.github.pureconfig" %% "pureconfig"                    % pureConfigVersion,
    "org.tpolecat"          %% "doobie-scalatest"              % doobieVersion          % Test,
    "com.codecommit"        %% "cats-effect-testing-scalatest" % catsEffectTestVersions % Test,
    "io.chrisdavenport"     %% "log4cats-slf4j"                % logForCatVersion
  ),
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion),
  libraryDependencies ++= Seq(
    "org.flywaydb"   % "flyway-maven-plugin" % "7.8.2",
    "com.h2database" % "h2"                  % "1.4.197"
  )
)

lazy val persistenceService = (project in file("modules/persistenceService"))
  .settings(
    moduleName := "persistenceService",
    Test / fork := true,
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core"                     % doobieVersion,
      "org.tpolecat" %% "doobie-postgres"                 % doobieVersion,
      "org.tpolecat" %% "doobie-specs2"                   % doobieVersion,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % testContainersScalaVersion % "test",
      "com.dimafeng" %% "testcontainers-scala-scalatest"  % testContainersScalaVersion % "test"
    ),
    applicationSettings
  )

lazy val algebrasAndModel = (project in file("modules/algebrasAndModel"))
  .settings(moduleName := "algebrasAndModel", applicationSettings)

lazy val users = (project in file("modules/users"))
  .settings(moduleName := "users", applicationSettings)
  .dependsOn(
    algebrasAndModel
  )

lazy val root = (project in file("."))
  .settings(
    name := "dummyProject",
    applicationSettings
  )
  .dependsOn(
    persistenceService,
    algebrasAndModel
  )
  .aggregate(
    persistenceService,
    algebrasAndModel,
    users
  )
