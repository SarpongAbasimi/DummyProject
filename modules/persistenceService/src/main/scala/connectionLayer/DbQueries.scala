package connectionLayer

import persistenceAlgebra.DbAlgebra
import persistenceModel.{User, UserName}
import doobie._
import doobie.implicits._
import cats.effect.Bracket

class DbQueries[F[_]](implicit ev: Bracket[F, Throwable]) extends DbAlgebra {
  def insert(user: User): ConnectionIO[Int] =
    sql"insert into user (name) values (${user.name})".update.run

  def find(user: User): ConnectionIO[Int] =
    sql"select from user where name = ${user.name}".update.run

  def remove(user: User): ConnectionIO[Int] =
    sql"delete from user where name = ${user.name}".update.run
}
