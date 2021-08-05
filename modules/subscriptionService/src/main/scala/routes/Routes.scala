package routes

import cats.effect.Sync
import mockedSubscriptionResponse.MockedResponse
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._

object Routes {

  def subscription[F[_]: Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root =>
      Ok("subscription service")
    }
  }

  def userSubscription[F[_]: Sync](): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> subscription / user =>
      Ok(MockedResponse.mockedUserSubscriptionResponse)
    }
  }
}
