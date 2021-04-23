val http4sVersion = "0.21.21"
val circeVersion  = "0.12.3"

lazy val applicationSettings = Seq(
  version := "0.1",
  scalaVersion := "2.13.5",
  libraryDependencies ++= Seq(
    "org.http4s"     %% "http4s-dsl"                    % http4sVersion,
    "org.http4s"     %% "http4s-blaze-server"           % http4sVersion,
    "org.http4s"     %% "http4s-blaze-client"           % http4sVersion,
    "com.codecommit" %% "cats-effect-testing-scalatest" % "1.0-25-c4685f2" % Test,
    "org.http4s"     %% "http4s-circe"                  % http4sVersion
  ),
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)
)

lazy val persistenceService = (project in file("modules/persistenceService"))
  .settings(
    moduleName := "persistenceService"
  )
  .settings(name := (moduleName.value))

lazy val algebrasAndModel = (project in file("modules/algebrasAndModel"))
  .settings(moduleName := "algebrasAndModel", applicationSettings)
  .settings(name := (moduleName.value))

lazy val users = (project in file("modules/users"))
  .settings(moduleName := "users", applicationSettings)
  .settings(name := (moduleName.value))
  .dependsOn(
    algebrasAndModel
  )

lazy val root = (project in file("."))
  .settings(
    name := "dummyProject",
    applicationSettings
  )
  .aggregate(
    persistenceService,
    algebrasAndModel,
    users
  )
