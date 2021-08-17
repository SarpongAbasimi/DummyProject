package service

import Errors.UserNotFound
import cats.effect.Sync
import userDbAlgebra.UserAlgebra
import subscriptionAlgebra.{SubscriptionAlgebra, SubscriptionServiceAlgebra}
import utils.Types.{GetSubscriptionData, Id, PostSubscriptions, SlackUserId}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.implicits._

object SubscriptionService {
  def implementation[F[_]: Sync](
      userAlgebra: UserAlgebra[ConnectionIO],
      subscriptionServiceAlgebra: SubscriptionServiceAlgebra[ConnectionIO],
      transactor: Transactor[F]
  ): SubscriptionAlgebra[F] = new SubscriptionAlgebra[F] {
    def getUserSubscriptions(slackUserId: SlackUserId): F[List[GetSubscriptionData]] = {
      for {
        user <- userAlgebra.findUser(slackUserId)
        optionOfGetSubscription <- user match {
          case None =>
            UserNotFound("User does not Exit").raiseError[ConnectionIO, List[GetSubscriptionData]]
          case Some(user) => subscriptionServiceAlgebra.get(user.id)
        }
      } yield optionOfGetSubscription
    }.transact(transactor)

    def postUserSubscriptions(slackUserId: SlackUserId, subscriptions: PostSubscriptions): F[Unit] =
      (for {
        checkIfTheUserExits <- userAlgebra.findUser(slackUserId)
        _ <- checkIfTheUserExits match {
          case None =>
            UserNotFound(s"Invalid: User with ${slackUserId.slackUserId} does not exit")
              .raiseError[ConnectionIO, Unit]

          case Some(user) => subscriptionServiceAlgebra.post(user.id, subscriptions)
        }
      } yield ()).transact(transactor)

    def deleteUserSubscription(
        slackUserId: SlackUserId,
        subscriptions: PostSubscriptions
    ): F[Unit] = (for {
      user <- userAlgebra.findUser(slackUserId)
      _ <- user match {
        case None =>
          UserNotFound(s"Invalid: User with ${slackUserId.slackUserId} does not exit")
            .raiseError[ConnectionIO, Unit]

        case Some(user) => subscriptionServiceAlgebra.delete(user.id, subscriptions)
      }
    } yield ()).transact(transactor)
  }
}
