package subsPersistenceLayer

import doobie.{ConnectionIO, Query0}
import doobie.implicits._
import subscriptionAlgebra.SubscriptionServiceAlgebra
import utils.Types.{
  GetSubscriptionData,
  Id,
  Owner,
  PostSubscriptionData,
  PostSubscriptions,
  Repository,
  RepositoryId
}
import cats.implicits._
import doobie.util.update.{Update, Update0}
import doobie.postgres.implicits._

object SubscriptionServicePersistenceLayer {
  val subscriptionServiceAlgImp: SubscriptionServiceAlgebra[ConnectionIO] =
    new SubscriptionServiceAlgebra[ConnectionIO] {
      def get(id: Id): ConnectionIO[List[GetSubscriptionData]] =
        SubscriptionServiceQuery.get(id).to[List]

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

      def delete(id: Id, subscriptions: PostSubscriptions): ConnectionIO[Unit] = for {
        _ <- subscriptions.subscriptions.traverse { data =>
          SubscriptionServiceQuery.delete(id, data.organization, data.repository).run.void
        }
      } yield ()
    }
}

object SubscriptionServiceQuery {
  def get(id: Id): Query0[GetSubscriptionData] =
    sql"select owner, repository, subscribed_at from repositories join subscriptions using (repository_id) where user_id = ${id.id}"
      .query[GetSubscriptionData]

  def post(subscriptions: PostSubscriptionData): doobie.Update0 =
    sql"insert into repositories(owner, repository) values(${subscriptions.organization}, ${subscriptions.repository})".update

  def insertInSubscription: Update[(Id, RepositoryId)] = {
    type SubscriptionInfo = (Id, RepositoryId)
    val sql = "insert into subscriptions(user_id, repository_id) values(?,?)"
    Update[SubscriptionInfo](sql)
  }
  def delete(id: Id, organization: Owner, repository: Repository): Update0 =
    sql"delete from subscriptions where user_id = ${id.id} and repository_id in (select repository_id from repositories where owner=${organization.owner} and repository=${repository.repository})".update
}
