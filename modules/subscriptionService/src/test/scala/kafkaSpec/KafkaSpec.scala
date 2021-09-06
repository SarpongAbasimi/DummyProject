package kafkaSpec

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.IO
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import doobie.util.transactor.Transactor
import kafka.{KafkaConsumerImplementation, KafkaProducerImplementation}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import utils.Types.OperationType.NewSubscription
import config.{ApplicationConfig, ConnectionUrl, DriverName, KafkaConfig, PassWord, User => DbUser}
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
import fs2.kafka.vulcan.{
  avroDeserializer,
  avroSerializer,
  AvroSettings,
  SchemaRegistryClientSettings
}
import fs2.kafka.{
  AutoOffsetReset,
  CommittableConsumerRecord,
  ConsumerSettings,
  KafkaConsumer,
  KafkaProducer,
  ProducerSettings,
  RecordSerializer,
  Serializer,
  Deserializer => ConsumerDeserializer
}
import kafka.KafkaConsumerImplementation.{avroSettings, messageDeserializer}
import migrations.DbMigrations
import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.common.serialization.Deserializer

import scala.concurrent.duration.DurationInt
//import net.manub.embeddedkafka.schemaregistry.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.scalatest.FutureOutcome

class KafkaSpec
    extends AsyncFreeSpec
    with AsyncIOSpec
    with Matchers
    with EmbeddedKafka
    with ForAllTestContainer {
  override lazy val container: PostgreSQLContainer = PostgreSQLContainer()

  lazy val transactor =
    Transactor.fromDriverManager[IO](
      container.driverClassName,
      container.jdbcUrl,
      container.username,
      container.password
    )

  override def withFixture(test: NoArgAsyncTest): FutureOutcome = {

    val driverName    = DriverName(container.driverClassName)
    val connectionUrl = ConnectionUrl(container.jdbcUrl)
    val user          = DbUser(container.username)
    val password      = PassWord(container.password)

    (for {
      _ <- DbMigrations
        .migrate[IO](
          ApplicationConfig(
            driverName,
            connectionUrl,
            user,
            password
          )
        )
      test <- IO(test())
    } yield test).unsafeRunSync()
  }

  "Kafka" - {
    "should receive a message event when published" in {
//      implicit val kafkaConfig = EmbeddedKafkaConfig(kafkaPort = 9092, schemaRegistryPort = 8081)

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
          producerSerializer <- Stream.eval(
            IO(
              avroSerializer[MessageEvent].using(
                AvroSettings(
                  SchemaRegistryClientSettings[IO](config.schemaRegistryUrl.schemaRegistryUrl)
                )
              )
            )
          )
          producerSettings <- Stream.eval(
            IO.pure(
              ProducerSettings(
                keySerializer = Serializer[IO, String],
                valueSerializer = producerSerializer
              ).withBootstrapServers("localhost:9092")
            )
          )
          producerResource <- Stream.resource(KafkaProducer.resource(producerSettings))
          _ <- Stream.eval(
            KafkaProducerImplementation
              .imp[IO](config, producerResource)
              .publish("1", MessageEvent(operationType, organization, repository))
          )
          _ <- Stream.eval(IO())
          consumerDeserializer <- Stream.eval(
            IO.pure(avroDeserializer[MessageEvent].using(AvroSettings {
              SchemaRegistryClientSettings[IO](config.schemaRegistryUrl.schemaRegistryUrl)
            }))
          )
          consumerSettings <- Stream.eval(
            IO(
              ConsumerSettings[IO, String, MessageEvent](
                keyDeserializer = ConsumerDeserializer[IO, String],
                valueDeserializer = consumerDeserializer
              ).withAutoOffsetReset(AutoOffsetReset.Earliest)
                .withBootstrapServers(config.bootstrapServer.bootstrapServer)
                .withGroupId(config.groupId.groupId)
            )
          )
          consumerResource          <- Stream.resource(KafkaConsumer.resource(consumerSettings))
          consumer                  <- Stream.eval(IO(KafkaConsumerImplementation.imp[IO](config, consumerResource)))
          committableConsumerRecord <- consumer.consume.take(1)
        } yield committableConsumerRecord

        consumedResult
          .map(_.record.value)
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
