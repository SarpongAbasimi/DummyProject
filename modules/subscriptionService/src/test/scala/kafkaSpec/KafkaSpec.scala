package kafkaSpec

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.IO
import config.KafkaConfig
import kafka.{KafkaConsumerImplementation, KafkaProducerImplementation}
import net.manub.embeddedkafka.schemaregistry.{EmbeddedKafka, EmbeddedKafkaConfig}
import scala.concurrent.duration._
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import utils.Types.OperationType.NewSubscription
import utils.Types.{
  BootstrapServer,
  GroupId,
  MessageEvent,
  Organization,
  Password,
  Repository,
  SchemaRegistryUrl,
  Topic,
  UserName
}
import fs2.Stream
import fs2.kafka.CommittableConsumerRecord

class KafkaSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with EmbeddedKafka {
  "Kafka" - {
    "should receive a message event when published" in {
      implicit val kafkaConfig: EmbeddedKafkaConfig =
        EmbeddedKafkaConfig(kafkaPort = 9092, schemaRegistryPort = 8081)

      withRunningKafka {
        val operationType = NewSubscription
        val organization  = Organization("47Degrees")
        val repository    = Repository("Scala Exercise")

        val config = KafkaConfig(
          Topic("dummyProject"),
          BootstrapServer("localhost:9092"),
          GroupId("publisher"),
          SchemaRegistryUrl("http://localhost:8081"),
          UserName("Ben"),
          Password("password")
        )

        val consumedResult: Stream[IO, CommittableConsumerRecord[IO, String, MessageEvent]] = for {
          _                         <- Stream.resource(KafkaProducerImplementation.resource[IO](config))
          committableConsumerRecord <- KafkaConsumerImplementation.imp[IO](config).consume.take(1)
        } yield committableConsumerRecord

        consumedResult
          .map(_.record.value)
          .interruptAfter(10.seconds)
          .compile
          .lastOrError
          .asserting { listOfMessageEvent =>
            listOfMessageEvent.operationType shouldBe operationType
            listOfMessageEvent.organization shouldBe organization
            listOfMessageEvent.repository shouldBe repository
          }
      }
    }

  }

}
