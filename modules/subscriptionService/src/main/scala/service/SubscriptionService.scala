package service

import Errors.UserNotFound
import cats.data.OptionT
import cats.effect.Sync
import userDbAlgebra.UserAlgebra
import subscriptionAlgebra.{SubscriptionAlgebra, SubscriptionServiceAlgebra}
import utils.Types.{
  GetSubscriptionData,
  MessageEvent,
  Organization,
  PostSubscriptions,
  SlackUserId,
  User
}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.implicits._
import kafkaAlgebra.KafkaProducerAlgebra
import utils.Types.OperationType.{DeleteSubscription, NewSubscription}

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
      dbOperation(slackUserId, subscriptions).transact(transactor).flatMap { user =>
        subscriptions.subscriptions.traverse_ { subs =>
          kafkaProducer.publish(
            user.id.id.toString,
            MessageEvent(
              NewSubscription,
              Organization(subs.organization.owner),
              subs.repository
            )
          )
        }
      }

    def deleteUserSubscription(
        slackUserId: SlackUserId,
        subscriptions: PostSubscriptions
    ): F[Unit] = {
      dbOperation(slackUserId, subscriptions).transact(transactor).flatMap { user =>
        subscriptions.subscriptions.traverse_ { subs =>
          kafkaProducer.publish(
            user.id.id.toString,
            MessageEvent(
              DeleteSubscription,
              Organization(subs.organization.owner),
              subs.repository
            )
          )
        }
      }
    }

    private def dbOperation(
        slackUserId: SlackUserId,
        subscriptions: PostSubscriptions
    ): ConnectionIO[User] =
      OptionT[ConnectionIO, User](userAlgebra.findUser(slackUserId))
        .semiflatTap(user => subscriptionServiceAlgebra.delete(user.id, subscriptions))
        .getOrElseF(
          UserNotFound(s"Invalid: User with ${slackUserId.slackUserId} does not exit")
            .raiseError[ConnectionIO, User]
        )
  }
}
