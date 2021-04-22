name := "dummyProject"

version := "0.1"

scalaVersion := "2.13.5"
val http4sVersion = "0.21.21"
val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "com.codecommit" %% "cats-effect-testing-scalatest" % "1.0-25-c4685f2" % Test,
  "org.http4s" %% "http4s-circe" % http4sVersion
)
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"

).map(_ % circeVersion)