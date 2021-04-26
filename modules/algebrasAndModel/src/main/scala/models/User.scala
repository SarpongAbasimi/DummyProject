package models
import utils.Types._

sealed trait GitHubUser extends Product with Serializable

final case class User(userName: Name) extends GitHubUser
