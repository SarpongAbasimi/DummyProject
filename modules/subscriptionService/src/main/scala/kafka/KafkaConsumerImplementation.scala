package kafka

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import config.KafkaConfig
import fs2.kafka.{
  AutoOffsetReset,
  CommittableConsumerRecord,
  ConsumerSettings,
  Deserializer,
  KafkaConsumer,
  RecordDeserializer
}
import fs2.kafka.vulcan.{avroDeserializer, AvroSettings, SchemaRegistryClientSettings}
import kafkaAlgebra.KafkaConsumerAlgebra
import utils.Types.MessageEvent
import fs2.Stream
import cats.implicits._

object KafkaConsumerImplementation {

  def resource[F[_]: ConcurrentEffect: ContextShift: Timer](
      kafkaConfig: KafkaConfig
  ): Resource[F, KafkaConsumerAlgebra[F, String, MessageEvent]] = KafkaConsumer[F]
    .resource[String, MessageEvent](consumerSettings(kafkaConfig))
    .map(consumer => imp[F](kafkaConfig, consumer))

  def imp[F[_]: Sync: ConcurrentEffect: ContextShift: Timer](
      kafkaConfig: KafkaConfig,
      consumer: KafkaConsumer[F, String, MessageEvent]
  ): KafkaConsumerAlgebra[F, String, MessageEvent] =
    new KafkaConsumerAlgebra[F, String, MessageEvent] {

      def consume: Stream[F, CommittableConsumerRecord[F, String, MessageEvent]] =
        Stream(consumer).evalTap(_.subscribeTo(kafkaConfig.topic.topic)).flatMap { kafkaConsumer =>
          Stream.eval(
            Sync[F].delay(s"About to consume data from topic: ${kafkaConfig.topic.topic}")
          ) *> kafkaConsumer.stream
        }
    }

  private def avroSettings[F[_]: Sync](kafkaConfig: KafkaConfig): AvroSettings[F] =
    AvroSettings {
      SchemaRegistryClientSettings[F](kafkaConfig.schemaRegistryUrl.schemaRegistryUrl)
    }
  private def messageDeserializer[F[_]: Sync](
      kafkaConfig: KafkaConfig
  ): RecordDeserializer[F, MessageEvent] =
    avroDeserializer[MessageEvent].using(avroSettings(kafkaConfig))

  private def consumerSettings[F[_]: Sync](
      kafkaConfig: KafkaConfig
  ): ConsumerSettings[F, String, MessageEvent] =
    ConsumerSettings[F, String, MessageEvent](
      keyDeserializer = Deserializer[F, String],
      valueDeserializer = messageDeserializer(kafkaConfig)
    ).withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers(kafkaConfig.bootstrapServer.bootstrapServer)
      .withGroupId(kafkaConfig.groupId.groupId)

}
