package kafkaAlgebra

import cats.effect.Sync
import fs2.kafka.ProducerSettings
import fs2.kafka.vulcan.{Auth, AvroSettings, SchemaRegistryClientSettings}
import utils.Types.{MessageEvent, Password, SchemaRegistryUrl, UserName}

trait KafkaAlgebra[F[_]] {
  def producerSettings: ProducerSettings[F, String, MessageEvent]

  def avroSettings(
      schemaRegistryUrl: SchemaRegistryUrl,
      userName: UserName,
      password: Password
  )(implicit
      sync: Sync[F]
  ): AvroSettings[F] =
    AvroSettings {
      SchemaRegistryClientSettings[F](schemaRegistryUrl.schemaRegistryUrl)
        .withAuth(Auth.Basic(userName.userName, password.password))
    }
}
