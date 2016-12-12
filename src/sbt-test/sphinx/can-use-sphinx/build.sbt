name := "test"

//#enablePlugin
enablePlugins(SphinxPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/giza`
siteSubdirName in Sphinx := "giza"
//#siteSubdirName

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Sphinx).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("SBT")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
