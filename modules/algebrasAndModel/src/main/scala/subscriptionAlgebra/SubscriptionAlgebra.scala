package subscriptionAlgebra

import utils.Types.{GetSubscriptionData, PostSubscriptions, SlackUserId}

trait SubscriptionAlgebra[F[_]] {
  def getUserSubscriptions(slackUserId: SlackUserId): F[List[GetSubscriptionData]]
  def postUserSubscriptions(slackUserId: SlackUserId, subscriptions: PostSubscriptions): F[Unit]
  def deleteUserSubscription(slackUserId: SlackUserId, subscriptions: PostSubscriptions): F[Unit]
}
