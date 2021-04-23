package models
import utils.Utils._

sealed trait GitHubRepository

final case class Repository(id: Id, name: Name, tag: Tag) extends GitHubRepository
