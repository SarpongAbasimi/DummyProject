package subscriptionAlgebra

import utils.Types.{GetSubscriptionData, Id, PostSubscriptions}

trait SubscriptionServiceAlgebra[F[_]] {
  def get(id: Id): F[List[GetSubscriptionData]]
  def post(id: Id, subscriptions: PostSubscriptions): F[Unit]
  def delete(id: Id, subscriptions: PostSubscriptions): F[Unit]
}
