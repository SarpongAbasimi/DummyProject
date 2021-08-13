package propertyTests
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import config.{ApplicationConfig, ConnectionUrl, DriverName, PassWord}
import connectionLayer.{DbConnection, UserAlgebra}
import migrations.DbMigrations
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatestplus.scalacheck._
import org.scalatest.matchers.should.Matchers
import utils.Types.User
import config.{User => ConfigUser}
import java.util.UUID
import doobie.implicits._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.BeforeAndAfter
import utils.Types.{Id, SlackChannelId, SlackUserId}
import UserAlgebra._

class PropertySpec
    extends AsyncFreeSpec
    with AsyncIOSpec
    with ScalaCheckDrivenPropertyChecks
    with ForAllTestContainer
    with BeforeAndAfter
    with Matchers {

  override val container: PostgreSQLContainer = PostgreSQLContainer()

  lazy val driverName    = DriverName(container.driverClassName)
  lazy val connectionUrl = ConnectionUrl(container.jdbcUrl)
  lazy val user          = ConfigUser(container.username)
  lazy val password      = PassWord(container.password)

  before {
    DbMigrations
      .migrate[IO](ApplicationConfig(driverName, connectionUrl, user, password))
      .map(_.migrationsExecuted)
      .unsafeRunSync()
  }

  "Migration" - {

    "after creating user table" - {
      "should allows user to be inserted into userdb" in {

        implicit val slackUserIdArb: Arbitrary[SlackUserId] =
          Arbitrary(Gen.identifier.map(SlackUserId(_)))

        forAll { (id: UUID, slackUserId: SlackUserId, slackChannelId: UUID) =>
          val theUserID         = Id(id)
          val theSlackChannelId = SlackChannelId(slackChannelId)
          val theUser           = User(theUserID, slackUserId, theSlackChannelId)

          val result: IO[Option[User]] = for {
            _ <- IO(println("Starting Migrations..."))
            dbConnection = new DbConnection[IO](
              ApplicationConfig(driverName, connectionUrl, user, password)
            )
            _ <- userAlgebraImplementation.insertUser(theUser).transact(dbConnection.connection)
            foundUser <- userAlgebraImplementation
              .findUser(theUser.id)
              .transact(dbConnection.connection)
          } yield foundUser

          result.asserting(e => e shouldBe (Some(theUser))).unsafeRunSync()

        }
      }

      "should allows user to be deleted from the userdb" in {

        implicit val slackUserIdArb: Arbitrary[SlackUserId] =
          Arbitrary(Gen.identifier.map(SlackUserId(_)))

        forAll { (id: UUID, slackUserId: SlackUserId, slackChannelId: UUID) =>
          val theUserID         = Id(id)
          val theSlackChannelId = SlackChannelId(slackChannelId)
          val theUser           = User(theUserID, slackUserId, theSlackChannelId)

          val result: IO[Option[User]] = for {
            _ <- IO(println("Starting Migrations..."))
            dbConnection = new DbConnection[IO](
              ApplicationConfig(driverName, connectionUrl, user, password)
            )
            _ <- userAlgebraImplementation.insertUser(theUser).transact(dbConnection.connection)
            _ <- userAlgebraImplementation
              .deleteUser(theUser.id)
              .transact(dbConnection.connection)
            foundUser <- userAlgebraImplementation
              .findUser(theUser.id)
              .transact(dbConnection.connection)
          } yield foundUser

          result.asserting(e => e shouldBe None).unsafeRunSync()
        }
      }
    }
  }
}
