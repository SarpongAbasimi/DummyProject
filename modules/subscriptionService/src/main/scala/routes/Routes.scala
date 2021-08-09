package routes

import cats.effect.Sync
import mockedSubscriptionResponse.MockedResponse
import org.http4s.{EntityDecoder, EntityEncoder, Header, Headers, HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import utils.Types.{PostSubscriptions, SlackCommandRequestBody}
import utils.Subscription
import cats.implicits._

object Routes {

  def subscription[F[_]: Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def handleRequest[A <: Subscription](request: Request[F])(implicit
        entityDecoder: EntityDecoder[F, A],
        entityEncoder: EntityEncoder[F, A]
    ): F[Response[F]] = {
      request
        .attemptAs[A]
        .foldF(
          _ => BadRequest("Bad request"),
          message =>
            if (request.method.eq(POST)) {
              Created(message)
            } else {
              NoContent()
            }
        )
    }

    implicit val encoder: EntityEncoder[F, PostSubscriptions] =
      jsonEncoderOf[F, PostSubscriptions]
    implicit val decoder: EntityDecoder[F, PostSubscriptions] =
      jsonOf[F, PostSubscriptions]
    implicit val slackCommandRequestBodyEncoder: EntityEncoder[F, SlackCommandRequestBody] =
      jsonEncoderOf[F, SlackCommandRequestBody]
    implicit val slackCommandRequestBodyDecoder: EntityDecoder[F, SlackCommandRequestBody] =
      jsonOf[F, SlackCommandRequestBody]

    HttpRoutes.of[F] {
      case GET -> Root => Ok("subscription service")
      case GET -> Root / "subscription" / user =>
        Ok(MockedResponse.mockedGetUserSubscriptionResponse)
      case req @ POST -> Root / "subscription" / user =>
        handleRequest[PostSubscriptions](req)
      case req @ DELETE -> Root / "subscription" / user =>
        handleRequest[PostSubscriptions](req)
      case req @ POST -> Root / "subscription" / "slack" / command =>
        req
          .attemptAs[SlackCommandRequestBody]
          .foldF(
            _ => BadRequest(),
            _ => Accepted()
          )
    }
  }
}
