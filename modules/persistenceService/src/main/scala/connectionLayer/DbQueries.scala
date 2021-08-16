package connectionLayer

import userDbAlgebra.UserAlgebra
import utils.Types.{User}
import doobie._
import doobie.implicits._
import utils.Types.Id
import doobie.postgres.implicits._

object UserAlgebra {
  def userAlgebraImplementation: UserAlgebra[ConnectionIO] = new UserAlgebra[ConnectionIO] {
    def insertUser(user: User): ConnectionIO[Int] = DbQueries.insert(user).run

    def findUser(id: Id): ConnectionIO[Option[User]] = DbQueries.find(id).option

    def deleteUser(id: Id): ConnectionIO[Int] = DbQueries.remove(id).run
  }
}

object DbQueries {

  def insert(user: User): Update0 =
    sql"insert into userdb (id, slack_user_id, slack_channel_id) values (${user.id}, ${user.slackUserId}, ${user.slackChannelId})".update

  def find(id: Id): Query0[User] =
    sql"select id, slack_user_id, slack_channel_id from userdb where id = ${id.id}"
      .query[User]

  def remove(id: Id): Update0 =
    sql"delete from userdb where id = ${id.id}".update
}
