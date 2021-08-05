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
import org.http4s.dsl.io.POST

class RoutesSpec extends AsyncFunSpec with AsyncIOSpec with Matchers {

  describe("Routes") {
    describe("When a get request is made to getUserSubscription with a userId") {
      it("should respond with the right data") {
        {
          for {
            request  <- IO.pure(Request[IO](uri = Uri.uri("subscription/user")))
            response <- Routes.getUserSubscription[IO].orNotFound(request)
          } yield response
        }.asserting { result =>
          result.status.code shouldBe (200)
          result.as[Json].unsafeRunSync() shouldBe MockedResponse.mockedGetUserSubscriptionResponse
        }
      }
    }

    describe("When a post request is sent to  postUserSubscription") {
      describe("with the wrong request body") {
        it("should return a 400 status code") {
          {
            for {
              request <- IO.pure(
                Request[IO](
                  method = POST,
                  uri = Uri.uri("subscription/user")
                )
              )
              response <- Routes.postUserSubscription[IO].orNotFound(request)
            } yield response
          }.asserting(_.status.code shouldBe (400))
        }
      }
    }
  }
}
