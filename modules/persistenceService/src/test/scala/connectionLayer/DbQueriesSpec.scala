package connectionLayer

import doobie.{Transactor, Update0}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.{ConcurrentEffect, ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import config.{ApplicationConfig, ConnectionUrl, DriverName, PassWord, User => DbUser}
import migrations.DbMigrations
import org.scalatest.Outcome
import persistenceModel.{Id, Name, User, UserName}

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
      .unsafeRunSync()
    try test()
  }

  describe("Queries") {
    describe("insert") {
      describe("when called") {
        it("should be able to insert a resource in the db") {
          val id        = Id(UUID.randomUUID())
          val name      = Name("sarps")
          val userName  = UserName("Ben")
          val dummyUser = User(id, name, userName)

          check[Update0](DbQueries.insert(dummyUser))
        }
      }
    }

    describe("find") {
      describe("when called") {
        it("should be able to find a resource in the db") {
          val userName = UserName("Ben")

          check(DbQueries.find(userName))
        }
      }
    }

    describe("remove") {
      describe("when called") {
        it("should be able to remove a resource from the db") {
          val userName = UserName("Ben")

          check[Update0](DbQueries.remove(userName))
        }
      }
    }
  }
}
