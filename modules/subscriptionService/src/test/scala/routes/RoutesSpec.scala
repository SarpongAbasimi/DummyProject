package routes
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import connectionLayer.UserAlgebra
import doobie.util.transactor.Transactor
import io.circe.Json
import org.http4s.{Request, Uri}
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import org.http4s.circe._
import org.http4s.dsl.io.POST
import org.http4s.implicits._
import service.SubscriptionService
import subsPersistenceLayer.SubscriptionServicePersistenceLayer
import subscriptionAlgebra.{SubscriptionAlgebra}

class RoutesSpec extends AsyncFunSpec with AsyncIOSpec with Matchers with ForAllTestContainer {

  override lazy val container: PostgreSQLContainer = PostgreSQLContainer()

  lazy val subscriptionService: SubscriptionAlgebra[IO] = (for {
    xa <- IO.pure(
      Transactor.fromDriverManager[IO](
        container.driverClassName,
        container.jdbcUrl,
        container.username,
        container.password
      )
    )
    user         <- IO.pure(UserAlgebra.userAlgebraImplementation)
    subscription <- IO.pure(SubscriptionServicePersistenceLayer.subscriptionServiceAlgImp)
  } yield SubscriptionService.implementation[IO](user, subscription, xa)).unsafeRunSync()

  describe("Routes") {
    describe("subscription") {
      describe("when it receives a request with a userId") {
        it("should respond with the right response data and status code") {
          {
            for {
              request  <- IO.pure(Request[IO](uri = Uri.uri("subscription/user")))
              response <- Routes.subscription[IO](subscriptionService).orNotFound(request)
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

    describe("When a post request is sent to the user subscription route") {
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
              response <- Routes.subscription[IO](subscriptionService).orNotFound(request)
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
                uri = Uri.uri("subscription/user")
              ).withEntity(TestMockedResponse.mockedPostUserSubscriptionResponse)
            )
            response <- Routes.subscription[IO](subscriptionService).orNotFound(request)
          } yield response).asserting(_.status.code shouldBe (201))
        }
      }
    }

    describe("when a post request is sent to the subscription route") {
      describe("with a slack command") {
        it("should return the right response") {
          (for {
            request <- IO.pure(
              Request[IO](
                method = POST,
                uri = Uri.uri("subscription/slack/weather")
              ).withEntity(TestMockedResponse.mockedSlackCommandBody)
            )
            response <- Routes.subscription[IO](subscriptionService).orNotFound(request)
          } yield response).asserting(_.status.code shouldBe (202))
        }
      }
    }
  }
}
