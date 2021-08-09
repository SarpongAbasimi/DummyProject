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
        handleRequest[F, PostSubscriptions](req, "application/json")
      case req @ DELETE -> Root / "subscription" / user =>
        handleRequest[F, PostSubscriptions](req, "application/json")
      case req @ POST -> Root / "subscription" / "slack" / command =>
        req
          .withHeaders(
            Headers.of(
              Header("Content-Type", "application/x-www-form-urlencoded")
            )
          )
          .attemptAs[SlackCommandRequestBody]
          .foldF(
            error => BadRequest(s"An error occurred ${error}"),
            _ => Accepted()
          )
    }
  }

  private def handleRequest[F[_]: Sync, A <: Subscription](
      request: Request[F],
      contentType: String
  )(implicit
      entityDecoder: EntityDecoder[F, A],
      entityEncoder: EntityEncoder[F, A]
  ): F[Response[F]] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    request
      .withHeaders(Headers.of(Header("Content-Type", contentType)))
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
