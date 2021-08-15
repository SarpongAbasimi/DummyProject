package service

import cats.effect.Sync
import subscriptionAlgebra.SubscriptionServiceAlgebra
import utils.Types.{GetSubscriptionData, Id, PostSubscriptions}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

object SubscriptionService {
  def implementation[F[_]: Sync](
      subscriptionAlgebra: SubscriptionServiceAlgebra[ConnectionIO],
      transactor: Transactor[F]
  ): SubscriptionServiceAlgebra[F] = new SubscriptionServiceAlgebra[F] {
    def get(id: Id): F[Option[GetSubscriptionData]] =
      subscriptionAlgebra
        .get(id)
        .transact(transactor)

    def post(id: Id, subscriptions: PostSubscriptions): F[Unit] =
      subscriptionAlgebra.post(id, subscriptions).transact(transactor)

    def delete(id: Id, subscriptions: PostSubscriptions): F[Unit] =
      subscriptionAlgebra.delete(id, subscriptions).transact(transactor)
  }
}
