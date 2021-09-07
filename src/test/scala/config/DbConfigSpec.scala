package config

import cats.effect.IO

class DbConfigSpec extends ConfBaseSpec {
  "DbConfig" - {
    "loadApplicationConfig" in {
      ApplicationConfig
        .loadApplicationConfig[IO]("dummy.jdbc")
        .attempt
        .map(result => assert(result.isRight))
    }
  }
}
