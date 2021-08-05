package routes
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import io.circe.Json
import org.http4s.{Request, Uri}
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import org.http4s.implicits._
import org.http4s.circe._
import mockedSubscriptionResponse.MockedResponse

class RoutesSpec extends AsyncFunSpec with AsyncIOSpec with Matchers {

  describe("Routes") {

    describe("When a userId is passed to userSubscription") {
      it("should respond with the right data") {
        for {
          request  <- IO.pure(Request[IO](uri = Uri.uri("subscription/user")))
          response <- Routes.userSubscription[IO].orNotFound(request)
        } yield IO(response)
          .asserting { response =>
            response.status.code shouldBe (200)
            response.as[Json].unsafeRunSync() shouldBe (
              MockedResponse.mockedUserSubscriptionResponse
            )
          }
          .unsafeRunSync()
      }
    }
  }
}
