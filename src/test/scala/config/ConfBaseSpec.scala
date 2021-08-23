package config

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

trait ConfBaseSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers
