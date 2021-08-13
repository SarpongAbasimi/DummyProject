package subsPersistanceLayer

import doobie.{ConnectionIO, Query0}
import doobie.implicits._
import subscriptionAlgebra.SubscriptionServiceAlgebra
import utils.Types.{GetSubscriptionData, Id, PostSubscriptionData, PostSubscriptions}

object SubscriptionServicePersistenceLayer {
  def subscriptionServiceAlgImp: SubscriptionServiceAlgebra[ConnectionIO] =
    new SubscriptionServiceAlgebra[ConnectionIO] {
      def get(id: Id): ConnectionIO[Option[GetSubscriptionData]] =
        SubscriptionServiceQuery.get(id).option

      def post(id: Id, subscriptions: PostSubscriptions): ConnectionIO[Unit] = for {
        subs <- subscriptions.subscriptions
      } yield ???
    }
}

object SubscriptionServiceQuery {
  def get(id: Id): Query0[GetSubscriptionData] =
    fr"select * from repositories join subscriptions on repositories.repository_id  = subscriptions.id where id = ${id.id}"
      .query[GetSubscriptionData]
  def post(subscriptions: PostSubscriptionData): doobie.Update0 =
    fr"insert into repositories(owner, repository) values(${subscriptions.organization}, ${subscriptions.repository})".update
}
