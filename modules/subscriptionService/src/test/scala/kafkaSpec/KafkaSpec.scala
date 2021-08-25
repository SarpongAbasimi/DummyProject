package kafkaSpec

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{IO}
import config.KafkaConfig
import io.github.embeddedkafka.EmbeddedKafka
import kafka.KafkaImplementation
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

class KafkaSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with EmbeddedKafka {

  /** Currently Testing */

  "Kafka" - {
    "should receive a message event when published" in {

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

      KafkaImplementation.imp[IO](config).publish(1, messageEvent)

      ???
    }
  }
}
