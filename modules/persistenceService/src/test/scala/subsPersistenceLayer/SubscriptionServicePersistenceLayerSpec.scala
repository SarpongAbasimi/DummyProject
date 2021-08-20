package subsPersistenceLayer

import Base.BaseSpec
import cats.effect.IO
import config.{ApplicationConfig, ConnectionUrl, DriverName, PassWord, User => DbUser}
import doobie.util.update.Update0
import migrations.DbMigrations
import org.scalatest.Outcome
import utils.Types.{Id, Owner, PostSubscriptionData, Repository}
import java.util.UUID
import doobie.postgres.implicits._

class SubscriptionServicePersistenceLayerSpec extends BaseSpec {
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

  describe("SubscriptionServiceQuery") {
    describe("get") {
      describe("when called") {
        it("should return a list of repositories to the user") {
          val id    = Id(UUID.randomUUID())
          val query = SubscriptionServiceQuery.get(id)
          check(query)
        }
      }
    }

    describe("post") {
      describe("when called") {
        it("should subscribe to a list of repositories") {
          val owner                = Owner("47Degrees")
          val repository           = Repository("cats-effect")
          val postSubscriptionData = PostSubscriptionData(owner, repository)
          val query                = SubscriptionServiceQuery.post(postSubscriptionData)
          check[Update0](query)
        }
      }
    }

    describe("delete") {
      describe("when called") {
        it("should unsubscribe from a list of repositories") {
          val id    = Id(UUID.randomUUID())
          val query = SubscriptionServiceQuery.delete(id)
          check(query)
        }
      }
    }
  }
}
