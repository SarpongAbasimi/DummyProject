package kafka

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
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
  def imp[F[_]: Sync: ConcurrentEffect: ContextShift: Timer](
      kafkaConfig: KafkaConfig
  ): KafkaConsumerAlgebra[F, String, MessageEvent] =
    new KafkaConsumerAlgebra[F, String, MessageEvent] {

      implicit val messageDeserializer: RecordDeserializer[F, MessageEvent] =
        avroDeserializer[MessageEvent].using(avroSettings)

      def consume: Stream[F, CommittableConsumerRecord[F, String, MessageEvent]] =
        KafkaConsumer
          .stream(consumerSettings)
          .evalTap(_.subscribeTo(kafkaConfig.topic.topic))
          .flatMap { consumer =>
            Stream.eval(
              Sync[F].delay(s"About to consume data from topic: ${kafkaConfig.topic.topic}")
            ) *> consumer.stream
          }

      private def consumerSettings: ConsumerSettings[F, String, MessageEvent] =
        ConsumerSettings[F, String, MessageEvent](
          keyDeserializer = Deserializer[F, String],
          valueDeserializer = messageDeserializer
        ).withAutoOffsetReset(AutoOffsetReset.Earliest)
          .withBootstrapServers(kafkaConfig.bootstrapServer.bootstrapServer)
          .withGroupId(kafkaConfig.groupId.groupId)

      def avroSettings: AvroSettings[F] =
        AvroSettings {
          SchemaRegistryClientSettings[F](kafkaConfig.schemaRegistryUrl.schemaRegistryUrl)
        }
    }

}
