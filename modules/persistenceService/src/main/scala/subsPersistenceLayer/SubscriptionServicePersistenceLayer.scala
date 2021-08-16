package subsPersistenceLayer

import doobie.{ConnectionIO, Query0}
import doobie.implicits._
import subscriptionAlgebra.SubscriptionServiceAlgebra
import utils.Types.{GetSubscriptionData, Id, PostSubscriptionData, PostSubscriptions, RepositoryId}
import cats.implicits._
import doobie.util.update.Update
import doobie.postgres.implicits._

object SubscriptionServicePersistenceLayer {
  val subscriptionServiceAlgImp: SubscriptionServiceAlgebra[ConnectionIO] =
    new SubscriptionServiceAlgebra[ConnectionIO] {
      def get(id: Id): ConnectionIO[Option[GetSubscriptionData]] =
        SubscriptionServiceQuery.get(id).option

      def post(id: Id, subscriptions: PostSubscriptions): ConnectionIO[Unit] =
        for {
          subs <- subscriptions.subscriptions.traverse { postData =>
            SubscriptionServiceQuery
              .post(postData)
              .withUniqueGeneratedKeys[RepositoryId]("repository_id")
              .map(repoInfo => (id, repoInfo))
          }
          _ <- SubscriptionServiceQuery.insertInSubscription.updateMany(subs).void
        } yield ()

      def delete(id: Id, subscriptions: PostSubscriptions): ConnectionIO[Unit] =
        SubscriptionServiceQuery.delete(id).updateMany(subscriptions.subscriptions).void
    }
}

object SubscriptionServiceQuery {
  def get(id: Id): Query0[GetSubscriptionData] =
    sql"select owner, repository, subscribed_at::varchar from repositories join subscriptions using (repository_id) where user_id = ${id.id}"
      .query[GetSubscriptionData]

  def post(subscriptions: PostSubscriptionData): doobie.Update0 =
    sql"insert into repositories(owner, repository) values(${subscriptions.organization}, ${subscriptions.repository})".update

  def insertInSubscription: Update[(Id, RepositoryId)] = {
    type SubscriptionInfo = (Id, RepositoryId)
    val sql = "insert into subscriptions(id, repository_id) values(?,?)"
    Update[SubscriptionInfo](sql)
  }

  def delete(id: Id): Update[PostSubscriptionData] = {
    val sql =
      s"delete from subscriptions where id = ${id.id} and repository_id in (select * from repositories where owner = ? and repository = ?)"
    Update[PostSubscriptionData](sql)
  }
}
