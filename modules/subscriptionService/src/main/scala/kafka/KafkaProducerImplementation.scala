package kafka

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Sync}
import config.KafkaConfig
import fs2.kafka.{
  KafkaProducer,
  ProducerRecord,
  ProducerRecords,
  ProducerSettings,
  RecordSerializer,
  Serializer
}
import fs2.kafka.vulcan.{avroSerializer, AvroSettings, SchemaRegistryClientSettings}
import kafkaAlgebra.KafkaProducerAlgebra
import cats.implicits._
import utils.Types.MessageEvent

object KafkaProducerImplementation {

  def resource[F[_]: ConcurrentEffect: ContextShift: Sync](
      kafkaConfigurations: KafkaConfig
  ): Resource[F, KafkaProducerAlgebra[F, String, MessageEvent]] =
    KafkaProducer[F]
      .resource[String, MessageEvent](producerSettings[F](kafkaConfigurations))
      .map(producer => imp[F](kafkaConfigurations, producer))

  def imp[F[_]: Sync: ConcurrentEffect: ContextShift](
      kafkaConfig: KafkaConfig,
      kafkaProducer: KafkaProducer[F, String, MessageEvent]
  ): KafkaProducerAlgebra[F, String, MessageEvent] =
    new KafkaProducerAlgebra[F, String, MessageEvent] {
      def publish(
          key: String,
          messageEvent: MessageEvent
      ): F[Unit] = for {
        record <- Sync[F].delay(
          ProducerRecord[String, MessageEvent](
            kafkaConfig.topic.topic,
            key,
            messageEvent
          )
        )
        _ <- kafkaProducer.produce(ProducerRecords.one(record)) *> Sync[F].delay(
          println(s"Message Event has been emitted to ${kafkaConfig.topic.topic}")
        )
      } yield ()
    }

  private def avroSettings[F[_]: Sync](kafkaConfig: KafkaConfig): AvroSettings[F] = AvroSettings(
    SchemaRegistryClientSettings[F](kafkaConfig.schemaRegistryUrl.schemaRegistryUrl)
  )

  private def messageEventSerializer[F[_]: Sync](
      kafkaConfig: KafkaConfig
  ): RecordSerializer[F, MessageEvent] =
    avroSerializer[MessageEvent].using(avroSettings(kafkaConfig))

  private def producerSettings[F[_]: Sync](
      kafkaConfig: KafkaConfig
  ): ProducerSettings[F, String, MessageEvent] =
    ProducerSettings[F, String, MessageEvent](
      keySerializer = Serializer[F, String],
      valueSerializer = messageEventSerializer(kafkaConfig)
    ).withBootstrapServers(kafkaConfig.bootstrapServer.bootstrapServer)
}
