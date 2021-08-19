package Base

import cats.effect.{ConcurrentEffect, ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import doobie.util.transactor.Transactor
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scala.concurrent.ExecutionContext.global

trait BaseSpec
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

}
