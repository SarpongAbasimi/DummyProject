package persistenceAlgebra

import persistenceModel.{User, UserName}
import doobie._

trait DbAlgebra {
  def insert(user: User): ConnectionIO[Int]
  def find(user: User): ConnectionIO[Int]
  def remove(user: User): ConnectionIO[Int]
}
