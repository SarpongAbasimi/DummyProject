package subscriptionServiceAlbegra

import utils.Types.{GetSubscriptions, PostSubscriptions}

trait SubscriptionAlgebras[F[_]] {
  def getUserSubscription(userId: Int): F[GetSubscriptions]
  def postUserSubscription(userId: Int): F[PostSubscriptions]
  def deleteUserSubscription(userId: Int): F[PostSubscriptions]
}
