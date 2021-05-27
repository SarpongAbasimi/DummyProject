import cats.effect._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import scala.concurrent.ExecutionContext.global

object Server {

  def stream[F[_]: Timer: ConcurrentEffect]: Stream[F, ExitCode] =
    ???
}
