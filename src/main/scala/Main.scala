import cats.effect._
import Server._
import config.ApplicationConfig
import connectionLayer.DbConnection
import migrations.DbMigrations
import fs2.Stream

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = (for {
    config       <- Stream.eval(ApplicationConfig.loadApplicationConfig[IO]("dummy.jdbc"))
    _            <- Stream.eval(DbMigrations.migrate[IO](config))
    dbConnection <- Stream.eval(IO(new DbConnection[IO](config)))
    output       <- stream[IO](dbConnection)
  } yield output).compile.toList.map(_.headOption.getOrElse(ExitCode.Error))
}
