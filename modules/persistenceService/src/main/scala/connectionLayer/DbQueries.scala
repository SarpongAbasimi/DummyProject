package connectionLayer

import persistenceAlgebra.DbAlgebra
import persistenceModel.{User}
import doobie._
import doobie.implicits._
import cats.effect.Bracket
import doobie.postgres.implicits._

class DbQueries[F[_]](implicit ev: Bracket[F, Throwable]) extends DbAlgebra {
  def insert(user: User): ConnectionIO[Int] = DbQueries.insert(user).run

  def find(username: User): ConnectionIO[Option[User]] = DbQueries.find(username).option

  def remove(user: User): ConnectionIO[Int] = DbQueries.remove(user).run
}

object DbQueries {

  def insert(user: User): Update0 =
    sql"insert into userdb (id, slack_user_id, slack_channel_id) values (${user.id}, ${user.slackUserId}, ${user.slackChannelId})".update

  def find(user: User): Query0[User] =
    sql"select id, slack_user_id, slack_channel_id from userdb where id = ${user.id}"
      .query[User]

  def remove(user: User): Update0 =
    sql"delete from userdb where id = ${user.id}".update
}
