package cats

/** A lolcat is just a catnoid in the category of endofunctors. */
sealed trait LolCat

/** Module with meowness stuff. */
object LolCat {
  case object Enuf extends LolCat
  case class ImInUr(lol: String, lolz: LolCat) extends LolCat

  /** Implicitly meowness. */
  implicit val catnoid = new Catnoid[LolCat] {
    override def noes = Enuf
    override def canHas(ceilingCat: LolCat, basementCat: LolCat) =
      (ceilingCat, basementCat) match {
        case (Enuf, _)              => basementCat
        case (_, Enuf)              => ceilingCat
        case (ImInUr(lol, lolz), _) => ImInUr(lol, canHas(lolz, basementCat))
      }
  }
}
