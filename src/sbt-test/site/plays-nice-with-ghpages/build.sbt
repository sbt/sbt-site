name := "site ghpages test"

version := "0.0-ABCD"

ghpages.settings

git.remoteRepo := "git@github.com:metasim/sbt-site.git"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value
  val readme = dest / "README.html"
  assert(readme.exists, s"${readme.getAbsolutePath} did not exist")
  val content = IO.readLines(readme)
  assert(content.exists(_.contains(version.value)), s"Did not find version in:\n${content.mkString("\n")}")

  streams.value.log.info("Note: ghpages publishing not actually tested. Only API linking.")
}
