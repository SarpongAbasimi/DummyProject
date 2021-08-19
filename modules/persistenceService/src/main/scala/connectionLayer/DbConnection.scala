package connectionLayer
import doobie._
import cats.effect._
import config.ApplicationConfig

class DbConnection[F[_]: ContextShift: Async](config: ApplicationConfig) {

  def connection = Transactor.fromDriverManager[F](
    config.driver.name,
    config.url.url,
    config.user.user,
    config.password.password
  )
}
