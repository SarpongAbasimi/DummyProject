package connectionLayer

import persistenceAlgebra.DbAlgebra
import persistenceModel.{Id, Name, User, UserName}
import doobie._
import doobie.implicits._
import cats.effect.Bracket
import doobie.postgres.implicits._

import java.util.UUID

class DbQueries[F[_]](implicit ev: Bracket[F, Throwable]) extends DbAlgebra {
  def insert(user: User): ConnectionIO[Int] = DbQueries.insert(user).run

  def find(username: UserName): ConnectionIO[Option[User]] = DbQueries.find(username).option

  def remove(user: UserName): ConnectionIO[Int] = DbQueries.remove(user).run
}

object DbQueries {

  implicit val nameMeta: Meta[Name]     = Meta.StringMeta.timap[Name](Name(_))(_.name)
  implicit val userName: Meta[UserName] = Meta[String].timap[UserName](UserName(_))(_.username)
  implicit val idMeta: Meta[Id]         = Meta[UUID].timap(Id(_))(_.id)

  def insert(user: User): Update0 =
    sql"insert into userdb (id, name, username) values (${user.id}, ${user.name}, ${user.userName})".update

  def find(username: UserName): Query0[User] =
    sql"select id, name, username from userdb where username = $username"
      .query[User]

  def remove(userName: UserName): Update0 =
    sql"delete from userdb where name = $userName".update
}
