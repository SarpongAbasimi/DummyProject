package config

import cats.effect.IO

class KafkaConfigSpec extends ConfBaseSpec {
  "KafkaConfig" - {
    "loadKafkaConfig" - {
      "should be able to load Kafka config" in {
        KafkaConfig
          .loadKafkaConfig[IO]("kafka")
          .attempt
          .map { result =>
            assert(result.isRight)
          }
      }
    }
  }
}
