package config

import cats.effect.Sync
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import pureconfig._
import pureconfig.generic.semiauto.deriveConvert

sealed trait DbConfig extends Product with Serializable

final case class DriverName(name: String)           extends AnyVal
final case class ConnectionUrl(url: String)         extends AnyVal
final case class User(user: String)                 extends AnyVal
final case class PassWord(password: Option[String]) extends AnyVal

final case class ApplicationConfig(
    driverName: DriverName,
    connectionUrl: ConnectionUrl,
    user: User,
    passWord: PassWord
)

object ApplicationConfigConfig {
  def loadConfigFromGlobal[F[_]: Sync](configNameSpace: String): F[ApplicationConfig] = {
    Sync[F].delay {
      val configurations = ConfigFactory.load(configNameSpace)
      load[F](configurations)
    }
  }
  def load[F[_]: Sync](config: Config): F[ApplicationConfig] =
    Sync[F].delay(
      ConfigSource.fromConfig(config).loadOrThrow
    )
  implicit val configConvert: ConfigConvert[ApplicationConfig] = deriveConvert
}
