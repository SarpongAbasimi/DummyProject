package persistenceModel

sealed trait GithubUser                     extends Product with Serializable
final case class Id(id: Int)                extends AnyVal
final case class Name(name: String)         extends AnyVal
final case class UserName(username: String) extends AnyVal

final case class User(id: Id, name: Name, userName: UserName) extends GithubUser
