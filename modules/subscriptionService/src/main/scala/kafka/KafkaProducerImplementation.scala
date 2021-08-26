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
import fs2.kafka.vulcan.{avroSerializer, AvroSettings, SchemaRegistryClientSettings}
import kafkaAlgebra.KafkaProducerAlgebra
import cats.implicits._
import utils.Types.{MessageEvent}
import fs2.Stream

object KafkaProducerImplementation {

  def imp[F[_]: Sync: ConcurrentEffect: ContextShift](
      kafkaConfig: KafkaConfig
  ): KafkaProducerAlgebra[F, String, MessageEvent] =
    new KafkaProducerAlgebra[F, String, MessageEvent] {

      implicit val messageEventSerializer: RecordSerializer[F, MessageEvent] =
        avroSerializer[MessageEvent].using(
          avroSettings
        )

      def publish(
          key: String,
          messageEvent: MessageEvent
      ): Stream[F, Unit] = for {
        kafkaProducer: KafkaProducer.Metrics[F, String, MessageEvent] <- KafkaProducer.stream(
          producerSettings
        )
        record: ProducerRecord[String, MessageEvent] <- Stream.eval(
          Sync[F].delay(
            ProducerRecord[String, MessageEvent](
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

      private def producerSettings: ProducerSettings[F, String, MessageEvent] =
        ProducerSettings[F, String, MessageEvent](
          keySerializer = Serializer[F, String],
          valueSerializer = messageEventSerializer
        )
          .withBootstrapServers(
            kafkaConfig.bootstrapServer.bootstrapServer
          )

      private def avroSettings: AvroSettings[F] =
        AvroSettings {
          SchemaRegistryClientSettings[F](kafkaConfig.schemaRegistryUrl.schemaRegistryUrl)
        }
    }
}
