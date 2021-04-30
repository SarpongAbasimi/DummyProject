package config

import cats.effect.Sync
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import pureconfig._
import pureconfig.generic.auto._
import cats.implicits._

sealed trait DbConfig extends Product with Serializable

final case class DriverName(name: String)   extends AnyVal
final case class ConnectionUrl(url: String) extends AnyVal
final case class User(user: String)         extends AnyVal
final case class PassWord(password: String) extends AnyVal

final case class ApplicationConfig(
    driver: DriverName,
    url: ConnectionUrl,
    user: User,
    password: PassWord
)

object ApplicationConfig {

  def loadApplicationConfig[F[_]: Sync](
      configSettingName: String
  ): F[ApplicationConfig] =
    Sync[F].delay(ConfigFactory.load()).flatMap(c => loadConfig[F](c.getConfig(configSettingName)))

  def loadConfig[F[_]: Sync](config: Config): F[ApplicationConfig] =
    Sync[F].delay(ConfigSource.fromConfig(config).loadOrThrow[ApplicationConfig])
}
