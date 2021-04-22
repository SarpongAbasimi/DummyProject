import ApplicationEncodersDecoders._
import cats.effect.Async
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._

object ApplicationRoutes {

  def contributorsWrapper[F[_]: Async](githubClient : GitHubClient[F])  = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F]{
      case GET -> Root / "api"/ "contributors" / owner / repo => for {
        data <- githubClient.contributors(owner, repo)
        res <- Ok(data)
      }  yield res
    }
  }
}
