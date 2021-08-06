package routes
import cats.effect.{IO}
import cats.effect.testing.scalatest.AsyncIOSpec
import io.circe.Json
import org.http4s.{Request, Uri}
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import org.http4s.implicits._
import org.http4s.circe._
import org.http4s.dsl.io.POST
import fs2.Stream

class RoutesSpec extends AsyncFunSpec with AsyncIOSpec with Matchers {
  describe("Routes") {
    describe("getUserSubscription") {
      describe("when it receives a request with a userId") {
        it("should respond with the right response data and status code") {
          {
            for {
              request  <- IO.pure(Request[IO](uri = Uri.uri("subscription/user")))
              response <- Routes.subscription[IO].orNotFound(request)
            } yield response
          }.asserting { result =>
            result.status.code shouldBe (200)
            result
              .as[Json]
              .unsafeRunSync() shouldBe TestMockedResponse.mockedGetUserSubscriptionResponse
          }
        }
      }
    }

    describe("When a post request is sent to postUserSubscription") {
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
              response <- Routes.subscription[IO].orNotFound(request)
            } yield response
          }.asserting(_.status.code shouldBe (400))
        }
      }

      describe("with the correct body") {
        it("should return a 201 status code") {
          (for {
            request <- IO.pure(
              Request[IO](
                method = POST,
                uri = Uri.uri("someEndpoint")
              ).withEntity(TestMockedResponse.mockedPostUserSubscriptionResponse)
            )
            response <- Routes.subscription[IO].orNotFound(request)
          } yield response).asserting(_.status.code shouldBe (201))
        }
      }
    }
  }
}
