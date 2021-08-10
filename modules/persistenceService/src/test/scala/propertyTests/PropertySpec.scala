package propertyTests
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import config.{ApplicationConfig, ConnectionUrl, DriverName, PassWord}
import connectionLayer.{DbConnection, DbQueries}
import migrations.DbMigrations
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatestplus.scalacheck._
import org.scalatest.matchers.should.Matchers
import persistenceModel.{Id, SlackChannelId, SlackUserId, User}
import config.{User => ConfigUser}
import java.util.UUID
import doobie.implicits._
import org.scalatest.BeforeAndAfter

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

        forAll { (id: UUID, slackUserId: UUID, slackChannelId: UUID) =>
          val theUserID         = Id(id)
          val theSlackUserId    = SlackUserId(slackUserId)
          val theSlackChannelId = SlackChannelId(slackChannelId)
          val theUser           = User(theUserID, theSlackUserId, theSlackChannelId)

          val result: IO[Option[User]] = for {
            _ <- IO(println("Starting Migrations..."))
            dbQueries = new DbQueries[IO]
            dbConnection = new DbConnection[IO](
              ApplicationConfig(driverName, connectionUrl, user, password)
            )
            _         <- dbQueries.insert(theUser).transact(dbConnection.connection)
            foundUser <- dbQueries.find(theUser).transact(dbConnection.connection)
          } yield foundUser

          result.asserting(e => e shouldBe (Some(theUser))).unsafeRunSync()

        }
      }

      "should allows user to be deleted from the userdb" in {

        forAll { (id: UUID, slackUserId: UUID, slackChannelId: UUID) =>
          val theUserID         = Id(id)
          val theSlackUserId    = SlackUserId(slackUserId)
          val theSlackChannelId = SlackChannelId(slackChannelId)
          val theUser           = User(theUserID, theSlackUserId, theSlackChannelId)

          val result: IO[Option[User]] = for {
            _ <- IO(println("Starting Migrations..."))
            dbQueries = new DbQueries[IO]
            dbConnection = new DbConnection[IO](
              ApplicationConfig(driverName, connectionUrl, user, password)
            )
            _         <- dbQueries.insert(theUser).transact(dbConnection.connection)
            _         <- dbQueries.remove(theUser).transact(dbConnection.connection)
            foundUser <- dbQueries.find(theUser).transact(dbConnection.connection)
          } yield foundUser

          result.asserting(e => e shouldBe None).unsafeRunSync()
        }
      }
    }
  }
}
