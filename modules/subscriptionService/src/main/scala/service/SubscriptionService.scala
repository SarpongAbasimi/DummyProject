package service

import Errors.UserNotFound
import cats.effect.Sync
import userDbAlgebra.UserAlgebra
import subscriptionAlgebra.{SubscriptionAlgebra, SubscriptionServiceAlgebra}
import utils.Types.{
  GetSubscriptionData,
  MessageEvent,
  Organization,
  PostSubscriptions,
  Repository,
  SlackUserId
}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.implicits._
import kafkaAlgebra.KafkaProducerAlgebra
import utils.Types.OperationType.{DeleteSubscription}
import fs2.Stream

object SubscriptionService {
  def implementation[F[_]: Sync](
      userAlgebra: UserAlgebra[ConnectionIO],
      subscriptionServiceAlgebra: SubscriptionServiceAlgebra[ConnectionIO],
      transactor: Transactor[F],
      kafkaProducer: KafkaProducerAlgebra[F, String, MessageEvent]
  ): SubscriptionAlgebra[F] = new SubscriptionAlgebra[F] {
    def getUserSubscriptions(slackUserId: SlackUserId): F[List[GetSubscriptionData]] = {
      for {
        optionOfAUser <- userAlgebra.findUser(slackUserId)
        user          <- optionOfAUser.liftTo[ConnectionIO](UserNotFound("User does not Exit"))
        subscription  <- subscriptionServiceAlgebra.get(user.id)
      } yield subscription
    }.transact(transactor)

    def postUserSubscriptions(slackUserId: SlackUserId, subscriptions: PostSubscriptions): F[Unit] =
      (for {
        optionOfAUser <- userAlgebra.findUser(slackUserId)
        user <- optionOfAUser.liftTo[ConnectionIO](
          UserNotFound(
            s"Invalid: User with ${slackUserId.slackUserId} does not exit"
          )
        )
        _ <- subscriptionServiceAlgebra.post(user.id, subscriptions)
      } yield ()).transact(transactor)

    def deleteUserSubscription(
        slackUserId: SlackUserId,
        subscriptions: PostSubscriptions
    ): F[Unit] = (for {
      optionOfAUser <- userAlgebra.findUser(slackUserId)
      user <- optionOfAUser.liftTo[ConnectionIO](
        UserNotFound(s"Invalid: User with ${slackUserId.slackUserId} does not exit")
      )
      _ <- subscriptionServiceAlgebra.delete(user.id, subscriptions)
//      _ <- kafkaProducer.publish()
    } yield ()).transact(transactor)
  }
}
