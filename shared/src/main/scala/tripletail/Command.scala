package tripletail

sealed trait Command extends Product with Serializable

final case class SignUp(email: String) extends Command

final case class SignIn(license: String, email: String) extends Command

final case class AddPool(license: String, pool: Pool) extends Command

final case class ListSurfaces(license: String, poolId: Int) extends Command

final case class AddSurface(license: String, surface: Surface) extends Command