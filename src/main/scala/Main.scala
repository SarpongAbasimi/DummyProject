import cats.effect._
import Server._

object Main extends IOApp.Simple {
  override def run: IO[Unit] = stream[IO].compile.drain
}