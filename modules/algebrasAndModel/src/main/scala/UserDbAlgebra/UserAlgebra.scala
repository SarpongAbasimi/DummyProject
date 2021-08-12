package UserDbAlgebra

import utils.Types.{Id, User}

trait UserAlgebra[F[_]] {
  def insertUser(user: User): F[Int]
  def findUser(id: Id): F[Option[User]]
  def deleteUser(id: Id): F[Int]
}
