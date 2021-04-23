package algebras
import cats.Monad
import models.{Contributor, GitHubTag, OwnerDetails, User, UserDetails}

trait GitHubAlgebra[F[_]] {
  def getUserFromGitHub(userName: User): F[UserDetails]
  def listRepositoryTagsForAUser[B[_]: Monad](ownerDetails: OwnerDetails): F[B[GitHubTag]]
  def listRepositoryContributors[B[_]: Monad](ownerDetails: OwnerDetails): F[B[Contributor]]
  def listFollowersOfAUser[B[_]: Monad](user: User): F[B[UserDetails]]
}
