package kafka

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Sync}
import config.KafkaConfig
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords, ProducerSettings, RecordSerializer, Serializer}
import fs2.kafka.vulcan.{AvroSettings, SchemaRegistryClientSettings, avroSerializer}
import kafkaAlgebra.KafkaProducerAlgebra
import cats.implicits._
import utils.Types.MessageEvent
import fs2.Stream

object KafkaProducerImplementation {

  def resource[F[_]: Sync: ConcurrentEffect: ContextShift](kafkaConfig: KafkaConfig): Resource[F, KafkaProducerAlgebra[F, String, MessageEvent]] =
    KafkaProducer.resource(
    ProducerSettings[F, String, MessageEvent](
      keySerializer = Serializer[F, String],
      valueSerializer = avroSerializer[MessageEvent].using(
        AvroSettings(SchemaRegistryClientSettings[F](kafkaConfig.schemaRegistryUrl.schemaRegistryUrl))
      )
    )
      .withBootstrapServers(
        kafkaConfig.bootstrapServer.bootstrapServer
      )
  ).map(imp[F](kafkaConfig, _))

  def imp[F[_]: Sync: ConcurrentEffect: ContextShift](
      kafkaConfig: KafkaConfig,
      kafkaProducer: KafkaProducer.Metrics[F, String, MessageEvent]
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
        _ <-
          kafkaProducer.produce(ProducerRecords.one(record)) *> Sync[F].delay(
            println(s"Message Event has been emitted to ${kafkaConfig.topic.topic}")
          )

      } yield ()
    }
}
