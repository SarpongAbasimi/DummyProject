package kafka

import cats.effect.Sync
import config.KafkaConfig
import fs2.kafka.{ProducerSettings, RecordSerializer, Serializer}
import vulcan.{AvroError, Codec}
import fs2.kafka.vulcan.avroSerializer
import kafkaAlgebra.KafkaAlgebra
import utils.OperationType
import cats.implicits._
import utils.Types.{
  DeleteSubscription,
  MessageEvent,
  NewSubscription,
  Organization,
  Password,
  Repository,
  SchemaRegistryUrl,
  UserName
}

object KafkaImplementation {
  implicit val operationTypeCodec: Codec[OperationType] = Codec.enumeration[OperationType](
    name = "operationType",
    namespace = "com.OperationType",
    symbols = List("NewSubscription", "DeleteSubscription"),
    encode = {
      case NewSubscription    => "NewSubscription"
      case DeleteSubscription => "DeleteSubscription"
    },
    decode = {
      case "NewSubscription"    => Right(NewSubscription)
      case "DeleteSubscription" => Right(DeleteSubscription)
      case other                => Left(AvroError(s"$other is not an operationType"))
    }
  )

  implicit val messageEventCodec: Codec[MessageEvent] = Codec.record[MessageEvent](
    name = "MessageEvent",
    namespace = "com.MessageEvent"
  ) { fields =>
    (
      fields("operationType", _.operationType),
      fields("organization", _.organization.organization),
      fields("respository", _.repository.repository)
    ).mapN((organizationType, organisation, repository) =>
      MessageEvent(
        organizationType,
        Organization(organisation),
        Repository(repository)
      )
    )
  }

  def imp[F[_]: Sync](kafkaConfig: KafkaConfig) = new KafkaAlgebra[F] {
    implicit val messageEventSerializer: RecordSerializer[F, MessageEvent] =
      avroSerializer[MessageEvent].using(
        avroSettings(
          SchemaRegistryUrl(kafkaConfig.schemaRegistryUrl.schemaRegistryUrl),
          UserName(kafkaConfig.userName.userName),
          Password(kafkaConfig.password.password)
        )
      )

    def producerSettings: ProducerSettings[F, String, MessageEvent] =
      ProducerSettings[F, String, MessageEvent](
        keySerializer = Serializer[F, String],
        valueSerializer = messageEventSerializer
      )
        .withBootstrapServers(
          kafkaConfig.bootstrapServer.bootstrapServer
        )
  }
}
