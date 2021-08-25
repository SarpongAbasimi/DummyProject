package kafkaSpec

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.IO
import config.KafkaConfig
import kafka.{KafkaConsumerImplementation, KafkaProducerImplementation}
import net.manub.embeddedkafka.schemaregistry.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import utils.Types.{
  BootstrapServer,
  GroupId,
  MessageEvent,
  NewSubscription,
  Organization,
  Password,
  Repository,
  SchemaRegistryUrl,
  Topic,
  UserName
}
import fs2.Stream
class KafkaSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with EmbeddedKafka {

  "Kafka" - {
    "should receive a message event when published" in {
      implicit val config = EmbeddedKafkaConfig(kafkaPort = 9092, schemaRegistryPort = 8081)
      withRunningKafka {

        val operationType = NewSubscription
        val organization  = Organization("47Degrees")
        val repository    = Repository("Scala Exercise")
        val messageEvent  = MessageEvent(operationType, organization, repository)

        val config = KafkaConfig(
          Topic("dummyProject"),
          BootstrapServer("http://localhost:9092"),
          GroupId("publisher"),
          SchemaRegistryUrl("http://localhost:8081"),
          UserName("Ben"),
          Password("password")
        )

        val consumedResult = for {
          _                         <- KafkaProducerImplementation.imp[IO](config).publish("1", messageEvent)
          committableConsumerRecord <- KafkaConsumerImplementation.imp[IO](config).consume.take(1)
        } yield committableConsumerRecord

        consumedResult
          .map(_.record.value)
          .compile
          .toList
          .asserting { listOfMessageEvent =>
            listOfMessageEvent.head.operationType shouldBe operationType
            listOfMessageEvent.head.organization shouldBe organization
            listOfMessageEvent.head.repository shouldBe repository
          }
      }
    }

  }

}
