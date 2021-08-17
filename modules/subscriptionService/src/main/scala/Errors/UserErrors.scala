package Errors

sealed trait UserErrors                  extends Exception
case class UserNotFound(message: String) extends UserErrors
