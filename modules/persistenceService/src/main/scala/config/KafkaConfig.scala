package config
import cats.effect.Sync
import com.typesafe.config.{Config, ConfigFactory}
import cats.implicits._
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import utils.Types.{BootstrapServer, GroupId, Password, SchemaRegistryUrl, Topic, UserName}

case class KafkaConfig(
    topic: Topic,
    bootstrapServer: BootstrapServer,
    groupId: GroupId,
    schemaRegistryUrl: SchemaRegistryUrl,
    userName: UserName,
    password: Password
)

object KafkaConfig {
  def loadKafkaConfig[F[_]: Sync](configName: String): F[KafkaConfig] =
    Sync[F].delay(ConfigFactory.load()).flatMap(config => load[F](config.getConfig(configName)))

  def load[F[_]: Sync](config: Config): F[KafkaConfig] =
    Sync[F].delay(ConfigSource.fromConfig(config).loadOrThrow[KafkaConfig])
}
