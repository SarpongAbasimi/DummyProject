package routes

import cats.effect.Sync
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import utils.Types.{PostSubscriptions, SlackCommandRequestBody, SlackUserId}
import utils.Subscription
import subscriptionAlgebra.SubscriptionAlgebra
import cats.implicits._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object Routes {

  def subscription[F[_]: Sync](
      subscriptionAlgebra: SubscriptionAlgebra[F]
  ): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

//    def handleRequest[A <: Subscription](request: Request[F])(implicit
//        entityDecoder: EntityDecoder[F, A],
//        entityEncoder: EntityEncoder[F, A]
//    ): F[Response[F]] = {
//      request
//        .attemptAs[A]
//        .foldF(
//          _ => BadRequest("Bad request"),
//          message =>
//            if (request.method.eq(POST)) {
//              Created(message)
//            } else {
//              NoContent()
//            }
//        )
//    }

    implicit val encoder: EntityEncoder[F, PostSubscriptions] =
      jsonEncoderOf[F, PostSubscriptions]
    implicit val decoder: EntityDecoder[F, PostSubscriptions] =
      jsonOf[F, PostSubscriptions]
    implicit val slackCommandRequestBodyEncoder: EntityEncoder[F, SlackCommandRequestBody] =
      jsonEncoderOf[F, SlackCommandRequestBody]
    implicit val slackCommandRequestBodyDecoder: EntityDecoder[F, SlackCommandRequestBody] =
      jsonOf[F, SlackCommandRequestBody]

    HttpRoutes.of[F] {
      case GET -> Root / "subscription" / user =>
        for {
          subscriptionData <- subscriptionAlgebra.getUserSubscriptions(SlackUserId(user))
          response         <- Ok(subscriptionData)
        } yield response
      case req @ POST -> Root / "subscription" / user =>
        req
          .attemptAs[PostSubscriptions]
          .foldF(
            _ => BadRequest("Bad request"),
            success =>
              subscriptionAlgebra.postUserSubscriptions(
                SlackUserId(user),
                success
              ) *> Created("Resource Created")
          )
      case req @ DELETE -> Root / "subscription" / user =>
        req
          .attemptAs[PostSubscriptions]
          .foldF(
            _ => BadRequest("Bad request"),
            success =>
              subscriptionAlgebra.deleteUserSubscription(SlackUserId(user), success)
                *> NoContent()
          )
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
