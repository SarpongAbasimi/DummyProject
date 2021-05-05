package migrations
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import config.{ApplicationConfig, ConnectionUrl, DriverName, PassWord, User}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class PsqlMigrationsSpec
    extends AsyncFreeSpec
    with AsyncIOSpec
    with ForAllTestContainer
    with Matchers {
  override val container: PostgreSQLContainer = PostgreSQLContainer()

  "Postgres container" - {
    "should be started" in {
      val driverName    = DriverName(container.driverClassName)
      val connectionUrl = ConnectionUrl(container.jdbcUrl)
      val user          = User(container.username)
      val password      = PassWord(container.password)

      DbMigrations
        .migrate[IO](ApplicationConfig(driverName, connectionUrl, user, password))
        .map(_.migrationsExecuted shouldBe (1))
    }
  }
}
