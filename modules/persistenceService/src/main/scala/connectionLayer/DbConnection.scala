package connectionLayer
import doobie._
import cats.effect._
import config.ApplicationConfig

class DbConnection[F[_]: ContextShift](config: ApplicationConfig)(implicit async: Async[F]) {

  def connection = Transactor.fromDriverManager[F](
    config.driver.name,
    config.url.url,
    config.user.user,
    config.password.password.getOrElse("")
  )
}
