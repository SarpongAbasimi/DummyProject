package routes
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import config.{ApplicationConfig, ConnectionUrl, DriverName, PassWord, User => DbUser}
import connectionLayer.UserAlgebra
import doobie.util.transactor.Transactor
import io.circe.Json
import migrations.DbMigrations
import org.http4s.{Request, Uri}
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import org.http4s.circe._
import org.http4s.dsl.io.POST
import org.http4s.implicits._
import org.scalatest.FutureOutcome
import service.SubscriptionService
import subsPersistenceLayer.SubscriptionServicePersistenceLayer
import subscriptionAlgebra.SubscriptionAlgebra
import utils.Types.{Id, SlackChannelId, SlackUserId, User}
import doobie.implicits._
import java.util.UUID

class RoutesSpec extends AsyncFunSpec with AsyncIOSpec with Matchers with ForAllTestContainer {

  override lazy val container: PostgreSQLContainer = PostgreSQLContainer()

  lazy val transactor =
    Transactor.fromDriverManager[IO](
      container.driverClassName,
      container.jdbcUrl,
      container.username,
      container.password
    )

  override def withFixture(test: NoArgAsyncTest): FutureOutcome = {

    val driverName    = DriverName(container.driverClassName)
    val connectionUrl = ConnectionUrl(container.jdbcUrl)
    val user          = DbUser(container.username)
    val password      = PassWord(container.password)

    (for {
      _ <- DbMigrations
        .migrate[IO](
          ApplicationConfig(
            driverName,
            connectionUrl,
            user,
            password
          )
        )
      test <- IO(test())
    } yield test).unsafeRunSync()
  }

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
              user <- IO.pure(
                User(
                  Id(UUID.randomUUID()),
                  SlackUserId("U2147483699"),
                  SlackChannelId(UUID.randomUUID())
                )
              )
              _ <- UserAlgebra.userAlgebraImplementation
                .insertUser(user)
                .transact(transactor)
              post <- IO.pure(
                Request[IO](method = POST, uri = Uri.uri("subscription/U2147483699"))
                  .withEntity[Json](TestMockedResponse.mockedPostUserSubscriptionOne)
              )
              _        <- Routes.subscription[IO](subscriptionService).orNotFound(post)
              request  <- IO.pure(Request[IO](uri = Uri.uri("subscription/U2147483699")))
              response <- Routes.subscription[IO](subscriptionService).orNotFound(request)
            } yield response
          }.asserting { result =>
            result.status.code shouldBe 200
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
            user <- IO.pure(
              User(
                Id(UUID.randomUUID()),
                SlackUserId("U2147483697"),
                SlackChannelId(UUID.randomUUID())
              )
            )
            _ <- UserAlgebra.userAlgebraImplementation.insertUser(user).transact(transactor)
            request <- IO.pure(
              Request[IO](
                method = POST,
                uri = Uri.uri("subscription/U2147483697")
              ).withEntity(TestMockedResponse.mockedPostUserSubscriptionBodyTwo)
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
