package connectionLayer

import doobie.{Transactor, Update0}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.{ConcurrentEffect, ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import config.{ApplicationConfig, ConnectionUrl, DriverName, PassWord, User => DbUser}
import migrations.DbMigrations
import org.scalatest.Outcome
import utils.Types.User
import utils.Types.{Id, SlackChannelId, SlackUserId}

import java.util.UUID
import scala.concurrent.ExecutionContext.global

class DbQueriesSpec
    extends AnyFunSpec
    with Matchers
    with doobie.scalatest.IOChecker
    with ForAllTestContainer {

  implicit val cs: ContextShift[IO]           = IO.contextShift(global)
  implicit val ce: ConcurrentEffect[IO]       = IO.ioConcurrentEffect
  override val container: PostgreSQLContainer = PostgreSQLContainer()

  lazy val transactor = Transactor.fromDriverManager[IO](
    container.driverClassName,
    container.jdbcUrl,
    container.username,
    container.password
  )

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
        ignore("should be able to insert a resource in the db") {
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
        ignore("should be able to find a resource in the db") {
          val id = Id(UUID.randomUUID())

          check(DbQueries.find(id))
        }
      }
    }

    describe("remove") {
      describe("when called") {
        ignore("should be able to remove a resource from the db") {
          val id = Id(UUID.randomUUID())

          check[Update0](DbQueries.remove(id))
        }
      }
    }
  }
}
