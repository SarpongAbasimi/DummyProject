package connectionLayer

import Base.BaseSpec
import doobie.{Update0}
import cats.effect.{IO}
import config.{ApplicationConfig, ConnectionUrl, DriverName, PassWord, User => DbUser}
import migrations.DbMigrations
import org.scalatest.Outcome
import utils.Types.User
import utils.Types.{Id, SlackChannelId, SlackUserId}
import java.util.UUID

class DbQueriesSpec extends BaseSpec {

  override def withFixture(test: NoArgTest): Outcome = {

    val driverName    = DriverName(container.driverClassName)
    val connectionUrl = ConnectionUrl(container.jdbcUrl)
    val user          = DbUser(container.username)
    val password      = PassWord(container.password)

    DbMigrations
      .migrate[IO](ApplicationConfig(driverName, connectionUrl, user, password))
      .flatMap(_ => IO(test()))
      .unsafeRunSync()
  }

  describe("Queries") {
    describe("insert") {
      describe("when called") {
        it("should be able to insert a resource in the db") {
          val id             = Id(UUID.randomUUID())
          val slackId        = SlackUserId(UUID.randomUUID().toString)
          val slackChannelId = SlackChannelId(UUID.randomUUID())
          val dummyUser      = User(id, slackId, slackChannelId)

          check[Update0](DbQueries.insert(dummyUser))
        }
      }
    }

    describe("find") {
      describe("when called") {
        it("should be able to find a resource in the db") {
          val slackUserId = SlackUserId(UUID.randomUUID().toString)

          check(DbQueries.find(slackUserId))
        }
      }
    }

    describe("remove") {
      describe("when called") {
        it("should be able to remove a resource from the db") {
          val id = Id(UUID.randomUUID())

          check[Update0](DbQueries.remove(id))
        }
      }
    }
  }
}
