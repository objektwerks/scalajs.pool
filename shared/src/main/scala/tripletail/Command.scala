package tripletail

sealed abstract class Command

final case class SignUp(email: String) extends Command

final case class SignIn(license: String) extends Command