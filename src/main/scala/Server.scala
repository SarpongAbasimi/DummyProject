import routes.Routes
import cats.effect._
import fs2.Stream
import cats.implicits._
import connectionLayer.{DbConnection, UserAlgebra}
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import service.SubscriptionService
import subsPersistenceLayer.SubscriptionServicePersistenceLayer
import scala.concurrent.ExecutionContext.global

object Server {

  def stream[F[_]: Timer: ConcurrentEffect](dbConnection: DbConnection[F]): Stream[F, ExitCode] = {
    for {
      _ <- BlazeClientBuilder[F](global).stream
      userAlgebra         = UserAlgebra.userAlgebraImplementation
      subscriptionAlgebra = SubscriptionServicePersistenceLayer.subscriptionServiceAlgImp
      transactor          = dbConnection.connection

      subscriptionService = SubscriptionService.implementation[F](
        userAlgebra,
        subscriptionAlgebra,
        transactor
      )

      services = (Routes.subscription[F](subscriptionService)).orNotFound
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(5000, "localhost")
        .withHttpApp(services)
        .serve
    } yield exitCode
  }
}
