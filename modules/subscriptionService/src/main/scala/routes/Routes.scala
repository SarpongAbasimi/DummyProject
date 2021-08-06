package routes

import cats.effect.Sync
import mockedSubscriptionResponse.MockedResponse
import org.http4s.{EntityDecoder, EntityEncoder, Header, Headers, HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import utils.Types.PostSubscriptions
import utils.Subscription
import cats.implicits._

object Routes {

  def subscription[F[_]: Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    implicit val encoder: EntityEncoder[F, PostSubscriptions] =
      jsonEncoderOf[F, PostSubscriptions]
    implicit val decoder: EntityDecoder[F, PostSubscriptions] =
      jsonOf[F, PostSubscriptions]

    HttpRoutes.of[F] {
      case GET -> Root                         => Ok("subscription service")
      case GET -> subscription / user          => Ok(MockedResponse.mockedGetUserSubscriptionResponse)
      case req @ POST -> subscription / user   => handleRequest(req)
      case req @ DELETE -> subscription / user => handleRequest(req)
    }
  }

  private def handleRequest[F[_]: Sync, A <: Subscription](request: Request[F])(implicit
      entityDecoder: EntityDecoder[F, A],
      entityEncoder: EntityEncoder[F, A]
  ): F[Response[F]] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    request
      .withHeaders(Headers.of(Header("Content-Type", "application/json")))
      .attemptAs[A]
      .foldF(
        error => BadRequest(s"Bad request ${error}"),
        message =>
          if (request.method.eq(POST)) {
            Created(message)
          } else {
            NoContent()
          }
      )
  }
}
