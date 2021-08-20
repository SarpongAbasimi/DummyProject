package connectionLayer

import userDbAlgebra.UserAlgebra
import utils.Types.{Id, SlackUserId, User}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

object UserAlgebra {
  def userAlgebraImplementation: UserAlgebra[ConnectionIO] = new UserAlgebra[ConnectionIO] {
    def insertUser(user: User): ConnectionIO[Int] = DbQueries.insert(user).run

    def findUser(slackUserId: SlackUserId): ConnectionIO[Option[User]] =
      DbQueries.find(slackUserId).option

    def deleteUser(id: Id): ConnectionIO[Int] = DbQueries.remove(id).run
  }
}

object DbQueries {

  def insert(user: User): Update0 =
    sql"insert into userdb (id, slack_user_id, slack_channel_id) values (${user.id}, ${user.slackUserId}, ${user.slackChannelId})".update

  def find(slackUserId: SlackUserId): Query0[User] =
    sql"select id, slack_user_id, slack_channel_id from userdb where slack_user_id = ${slackUserId}"
      .query[User]

  def remove(id: Id): Update0 =
    sql"delete from userdb where id = ${id}".update
}
