name := "test"

//#enablePlugin
enablePlugins(NanocPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/conan`
siteSubdirName in Nanoc := "conan"
//#siteSubdirName

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Nanoc).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("Nanoc site")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
