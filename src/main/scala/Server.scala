import cats.effect._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import scala.concurrent.ExecutionContext.global

/**
 * This should prob be moved to a config file
 */
object HttpBinding {
  val port = 8080
  val host = "localhost"
}

object Server {
  import HttpBinding._
  import ApplicationRoutes._

  def stream[F[_]: Timer: ConcurrentEffect]: Stream[F, ExitCode] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      githubClient = GitHubClient.imp[F](client)
      services     = (contributorsWrapper[F](githubClient)).orNotFound
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(port, host)
        .withHttpApp(services)
        .serve
    } yield exitCode
  }
}
