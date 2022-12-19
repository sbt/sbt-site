name := "test"

//#enablePlugin
enablePlugins(SphinxPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/giza`
Sphinx / siteSubdirName := "giza"
//#siteSubdirName

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (Sphinx / siteSubdirName).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("sbt")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
