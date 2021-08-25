package kafka

import cats.effect.{ConcurrentEffect, ContextShift, Sync}
import config.KafkaConfig
import fs2.kafka.{
  KafkaProducer,
  ProducerRecord,
  ProducerRecords,
  ProducerSettings,
  RecordSerializer,
  Serializer
}
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
import fs2.Stream

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

  def imp[F[_]: Sync: ConcurrentEffect: ContextShift](
      kafkaConfig: KafkaConfig
  ): KafkaAlgebra[F, Int, MessageEvent] =
    new KafkaAlgebra[F, Int, MessageEvent] {
      implicit val messageEventSerializer: RecordSerializer[F, MessageEvent] =
        avroSerializer[MessageEvent].using(
          avroSettings(
            SchemaRegistryUrl(kafkaConfig.schemaRegistryUrl.schemaRegistryUrl),
            UserName(kafkaConfig.userName.userName),
            Password(kafkaConfig.password.password)
          )
        )

      def producerSettings: ProducerSettings[F, Int, MessageEvent] =
        ProducerSettings[F, Int, MessageEvent](
          keySerializer = Serializer[F, Int],
          valueSerializer = messageEventSerializer
        )
          .withBootstrapServers(
            kafkaConfig.bootstrapServer.bootstrapServer
          )

      def publish(
          key: Int,
          messageEvent: MessageEvent
      ): Stream[F, Unit] = for {
        kafkaProducer: KafkaProducer.Metrics[F, Int, MessageEvent] <- KafkaProducer.stream(
          producerSettings
        )
        record: ProducerRecord[Int, MessageEvent] <- Stream.eval(
          Sync[F].delay(
            ProducerRecord[Int, MessageEvent](
              kafkaConfig.topic.topic,
              key,
              messageEvent
            )
          )
        )
        _ <- Stream.eval(
          kafkaProducer.produce(ProducerRecords.one(record)) *> Sync[F].delay(
            println(s"Message Event has been emitted to ${kafkaConfig.topic.topic}")
          )
        )
      } yield ()
    }
}
