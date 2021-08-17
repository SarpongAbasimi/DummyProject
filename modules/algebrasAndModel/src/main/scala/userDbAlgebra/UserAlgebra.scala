package userDbAlgebra

import utils.Types.{Id, SlackUserId, User}

trait UserAlgebra[F[_]] {
  def insertUser(user: User): F[Int]
  def findUser(slackUserId: SlackUserId): F[Option[User]]
  def deleteUser(id: Id): F[Int]
}
