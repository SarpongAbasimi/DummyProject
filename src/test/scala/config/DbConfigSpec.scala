package config

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.IO
import org.scalatest.freespec.AsyncFreeSpec

class DbConfigSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "DbConfig" - {
    "loadApplicationConfig" in {
      ApplicationConfig
        .loadApplicationConfig[IO]("dummy.jdbc")
        .attempt
        .map(result => assert(result.isRight))
    }
  }
}
