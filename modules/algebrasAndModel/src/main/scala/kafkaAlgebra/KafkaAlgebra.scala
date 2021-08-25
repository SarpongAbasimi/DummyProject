package kafkaAlgebra

import cats.effect.Sync
import fs2.kafka.{ProducerRecords, ProducerSettings}
import fs2.kafka.vulcan.{Auth, AvroSettings, SchemaRegistryClientSettings}
import utils.Types.{Password, SchemaRegistryUrl, UserName}
import fs2.Stream

trait KafkaAlgebra[F[_], K, V] {

  def publish(key: K, messageEvent: V): Stream[F, Unit]
  def producerSettings: ProducerSettings[F, K, V]
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
