package service

import Errors.UserNotFound
import cats.effect.Sync
import userDbAlgebra.UserAlgebra
import subscriptionAlgebra.SubscriptionServiceAlgebra
import utils.Types.{GetSubscriptionData, Id, PostSubscriptions}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.implicits._

object SubscriptionService {
  def implementation[F[_]: Sync](
      userAlgebra: UserAlgebra[ConnectionIO],
      subscriptionAlgebra: SubscriptionServiceAlgebra[ConnectionIO],
      transactor: Transactor[F]
  ): SubscriptionServiceAlgebra[F] = new SubscriptionServiceAlgebra[F] {
    def get(id: Id): F[List[GetSubscriptionData]] = {
      for {
        user <- userAlgebra.findUser(id)
        optionOfGetSubscription <- user match {
          case None =>
            UserNotFound("User does not Exit").raiseError[ConnectionIO, List[GetSubscriptionData]]
          case Some(user) => subscriptionAlgebra.get(user.id)
        }
      } yield optionOfGetSubscription
    }.transact(transactor)

    def post(id: Id, subscriptions: PostSubscriptions): F[Unit] = (for {
      checkIfTheUserExits <- userAlgebra.findUser(id)
      _ <- checkIfTheUserExits match {
        case None =>
          Sync[ConnectionIO].raiseError(
            UserNotFound(s"Invalid: User with ${id.id} does not exit")
          )
        case Some(user) => subscriptionAlgebra.post(user.id, subscriptions)
      }
    } yield ()).transact(transactor)

    def delete(id: Id, subscriptions: PostSubscriptions): F[Unit] = (for {
      user <- userAlgebra.findUser(id)
      _ <- user match {
        case None =>
          Sync[ConnectionIO].raiseError(
            UserNotFound(s"Invalid: User with ${id.id} does not exit")
          )
        case Some(user) => subscriptionAlgebra.delete(user.id, subscriptions)
      }
    } yield ()).transact(transactor)
  }
}
