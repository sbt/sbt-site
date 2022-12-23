package cats

/** A catgroup with identity. */
trait Catnoid[A] {
  def noes: A
  def canHas(a: A, b: A): A
}
