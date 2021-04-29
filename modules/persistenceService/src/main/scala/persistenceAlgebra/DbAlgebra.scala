package persistenceAlgebra

import persistenceModel.{User, UserName}
import doobie._

trait DbAlgebra {
  def insert(user: User): ConnectionIO[Int]
  def find(username: UserName): ConnectionIO[Option[User]]
  def remove(user: UserName): ConnectionIO[Int]
}
