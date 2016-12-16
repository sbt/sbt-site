package kittens

import cats.Catnoid

/** A kitteh is just a catnoid in the category of endofunctors. */
case class Kitteh(ohHai: String)

/** Module with meowness stuff. */
object Kitteh {
  /** Implicitly kittehs. */
  implicit val catnoid = new Catnoid[Kitteh] {
    override def noes = Kitteh("")
    override def canHas(itteh: Kitteh, bitteh: Kitteh) =
      Kitteh(itteh.ohHai + bitteh.ohHai)
  }
}
