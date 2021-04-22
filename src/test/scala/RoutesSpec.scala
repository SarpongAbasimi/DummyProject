import cats.effect._
import cats.effect.testing.scalatest.AsyncIOSpec
import org.http4s.{Method, Request}
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec

/**
 * Test will be written after issue with sbt has been resolved
 * */
//class RoutesSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {
//  "ApplicationRoutes" - {
//    "hello route" - {
//      "when called works" in {
//        val request: Request[IO] = Request[IO](Method.GET, uri = uri"/contributors")
//      }
//    }
//  }
//}
