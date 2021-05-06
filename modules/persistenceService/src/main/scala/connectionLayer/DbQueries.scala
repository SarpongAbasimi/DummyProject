package connectionLayer

import persistenceAlgebra.DbAlgebra
import persistenceModel.{User, UserName}
import doobie._
import doobie.implicits._
import cats.effect.Bracket
import doobie.postgres.implicits._

class DbQueries[F[_]](implicit ev: Bracket[F, Throwable]) extends DbAlgebra {
  def insert(user: User): ConnectionIO[Int] = DbQueries.insert(user).run

  def find(username: UserName): ConnectionIO[Option[User]] = DbQueries.find(username).option

  def remove(user: UserName): ConnectionIO[Int] = DbQueries.remove(user).run
}

object DbQueries {

  def insert(user: User): Update0 =
    sql"insert into userdb (id, name, username) values (${user.id}, ${user.name}, ${user.userName})".update

  def find(username: UserName): Query0[User] =
    sql"select id, name, username from userdb where username = $username"
      .query[User]

  def remove(userName: UserName): Update0 =
    sql"delete from userdb where username = $userName".update
}
