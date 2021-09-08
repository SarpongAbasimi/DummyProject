package routes
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Sync}
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.implicits.http4sLiteralsSyntax

object SwaggerRoutes {

  def swagger[F[_]: Sync: ConcurrentEffect: ContextShift](blocker: Blocker): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root =>
        PermanentRedirect(Location(uri"swagger-ui/index.html"))
      case req @ GET -> path if path.startsWith(Path("swagger-ui")) =>
        val swaggerPathIndex = path.toList.indexOf("swagger-ui")
        val file             = path.toList.drop(swaggerPathIndex + 1).mkString("/")
        StaticFile.fromResource("/swagger-ui/" + file, blocker, Some(req)).getOrElseF(NotFound())
    }
  }
}
