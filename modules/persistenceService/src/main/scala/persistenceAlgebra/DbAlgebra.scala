package persistenceAlgebra

import persistenceModel.{User}
import doobie._

trait DbAlgebra {
  def insert(user: User): ConnectionIO[Int]
  def find(user: User): ConnectionIO[Option[User]]
  def remove(user: User): ConnectionIO[Int]
}
