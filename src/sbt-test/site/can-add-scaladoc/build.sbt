name := "scaladoc site test"

enablePlugins(SiteScaladocPlugin)

version := "0.0.meow"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in SiteScaladoc).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("cats.preowned.Meow")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
