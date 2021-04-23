package models
import utils.Utils._

sealed trait GitHubUser

final case class User(userName: Name) extends GitHubUser
