package routes

import cats.effect.Sync
import mockedSubscriptionResponse.MockedResponse
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import utils.Types.PostSubscriptions
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
      case GET -> Root                => Ok("subscription service")
      case GET -> subscription / user => Ok(MockedResponse.mockedGetUserSubscriptionResponse)
      case req @ POST -> subscription / user =>
        req.decode[PostSubscriptions] { message: PostSubscriptions =>
          Created(message)
        }
    }
  }
}
