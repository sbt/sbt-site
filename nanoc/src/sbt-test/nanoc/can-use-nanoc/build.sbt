name := "test"

//#enablePlugin
enablePlugins(SitePreviewPlugin, NanocPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/conan`
Nanoc / siteSubdirName := "conan"
//#siteSubdirName

TaskKey[Unit]("checkContent") := {
  val dest  = (makeSite / target).value / (Nanoc / siteSubdirName).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("Nanoc site")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
