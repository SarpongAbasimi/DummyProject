package connectionLayer

import doobie.{Transactor, Update0}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.{ConcurrentEffect, ContextShift, IO}
import persistenceModel.{Id, Name, User, UserName}

import java.util.UUID
import scala.concurrent.ExecutionContext.global

class DbQueriesSpec extends AnyFunSpec with Matchers with doobie.scalatest.IOChecker {
  implicit val cs: ContextShift[IO]     = IO.contextShift(global)
  implicit val ce: ConcurrentEffect[IO] = IO.ioConcurrentEffect

  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:dummyproject",
    "sabasimi",
    ""
  )

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
