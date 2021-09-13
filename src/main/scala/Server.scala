import routes.{Routes, SwaggerRoutes}
import cats.effect._
import cats.implicits._
import config.KafkaConfig
import fs2.Stream
import connectionLayer.{DbConnection, UserAlgebra}
import kafka.{KafkaConsumerImplementation, KafkaProducerImplementation}
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import service.SubscriptionService
import subsPersistenceLayer.SubscriptionServicePersistenceLayer

import scala.concurrent.ExecutionContext.global

object Server {

  def stream[F[_]: Timer: ConcurrentEffect: ContextShift: Sync](
      dbConnection: DbConnection[F],
      kafkaConfig: KafkaConfig
  ): Stream[F, ExitCode] = {
    for {

      _ <- BlazeClientBuilder[F](global).stream

      kafkaProducer <- Stream.resource(
        KafkaProducerImplementation.resource[F](kafkaConfig)
      )

      kafkaConsumer <- Stream.resource(
        KafkaConsumerImplementation.resource(kafkaConfig)
      )

      userAlgebra         = UserAlgebra.userAlgebraImplementation
      subscriptionAlgebra = SubscriptionServicePersistenceLayer.subscriptionServiceAlgImp
      transactor          = dbConnection.connection

      subscriptionService = SubscriptionService.implementation[F](
        userAlgebra,
        subscriptionAlgebra,
        transactor,
        kafkaProducer
      )
      blocker <- Stream.resource(Blocker[F])
      services = (Routes.subscription[F](subscriptionService) <+> SwaggerRoutes
        .swagger[F](blocker)).orNotFound
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(5000, "0.0.0.0")
        .withHttpApp(services)
        .serve
    } yield exitCode
  }
}
