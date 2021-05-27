package algebras
import models.{Contributor, GitHubTag, OwnerDetails, User, UserDetails}

trait GitHubAlgebra[F[_]] {
  def getUserFromGitHub(userName: User): F[GitHubTag]
  def listRepositoryData(ownerDetails: OwnerDetails): F[List[Contributor]]
  def listFollowersOfAUser(user: User): F[List[UserDetails]]
}
