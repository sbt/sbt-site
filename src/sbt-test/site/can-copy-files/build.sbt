name := "test"

version := "0.0-ABCD"

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value
  val readme = dest / "README.html"
  assert(readme.exists, s"${readme.getAbsolutePath} did not exist")
  val content = IO.readLines(readme)
  assert(content.exists(_.contains(version.value)), s"Did not find version in:\n${content.mkString("\n")}")
}
