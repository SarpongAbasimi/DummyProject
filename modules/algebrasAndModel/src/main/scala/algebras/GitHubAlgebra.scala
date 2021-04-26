package algebras
import models.{OwnerDetails, User, UserDetails}

trait GitHubAlgebra[F[_]] {
  def getUserFromGitHub(userName: User): F[UserDetails]
  def listRepositoryData[B[_], A](ownerDetails: OwnerDetails): F[List[A]]
  def listFollowersOfAUser[B[_], A](user: User): F[List[A]]
}
