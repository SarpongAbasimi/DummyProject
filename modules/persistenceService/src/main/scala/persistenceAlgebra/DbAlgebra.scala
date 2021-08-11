package persistenceAlgebra

import persistenceModel.{Id, User}
import doobie._

trait DbAlgebra {
  def insert(user: User): ConnectionIO[Int]
  def find(id: Id): ConnectionIO[Option[User]]
  def remove(id: Id): ConnectionIO[Int]
}
