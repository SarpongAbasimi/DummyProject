package config

import cats.effect.Sync
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import pureconfig._
import pureconfig.generic.auto._

sealed trait DbConfig extends Product with Serializable

final case class DriverName(name: String)           extends AnyVal
final case class ConnectionUrl(url: String)         extends AnyVal
final case class User(user: String)                 extends AnyVal
final case class PassWord(password: Option[String]) extends AnyVal

final case class ApplicationConfig(
    driver: DriverName,
    url: ConnectionUrl,
    user: User,
    password: PassWord
)

object ApplicationConfig {

  def loadApplicationConfig[F[_]: Sync](
      configSettingName: String
  ): F[ConfigReader.Result[ApplicationConfig]] =
    Sync[F].defer {
      val config: Config = ConfigFactory.load()
      loadConfig[F](config.getConfig(configSettingName))
    }

  def loadConfig[F[_]: Sync](config: Config) =
    Sync[F].delay(ConfigSource.fromConfig(config).load[ApplicationConfig])
}
