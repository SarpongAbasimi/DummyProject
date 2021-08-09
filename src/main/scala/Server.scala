import routes.Routes
import cats.effect._
import fs2.Stream
import cats.implicits._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import scala.concurrent.ExecutionContext.global

object Server {

  def stream[F[_]: Timer: ConcurrentEffect]: Stream[F, ExitCode] = {
    for {
      _ <- BlazeClientBuilder[F](global).stream
      services = (Routes.subscription[F]).orNotFound
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(5000, "localhost")
        .withHttpApp(services)
        .serve
    } yield exitCode
  }
}
