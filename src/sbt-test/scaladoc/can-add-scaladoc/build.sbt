name := "scaladoc site test"

//#enablePlugin
enablePlugins(SiteScaladocPlugin)
//#enablePlugin

//#siteSubdirName
// Puts Scaladoc output in `target/site/api/latest`
SiteScaladoc / siteSubdirName := "api/latest"
//#siteSubdirName

version := "0.0.meow"

scalaVersion := "2.12.17"

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (SiteScaladoc / siteSubdirName).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("cats.preowned.Meow")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
