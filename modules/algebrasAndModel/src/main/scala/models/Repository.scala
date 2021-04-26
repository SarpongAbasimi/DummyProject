package models
import utils.Types._

sealed trait GitHubRepository extends Product with Serializable

final case class Repository(id: Id, name: Name, tag: Tag) extends GitHubRepository
