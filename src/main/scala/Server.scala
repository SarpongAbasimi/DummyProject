import cats.effect._
import fs2.Stream

object Server {

  def stream[F[_]: Timer: ConcurrentEffect]: Stream[F, ExitCode] =
    ???
}
