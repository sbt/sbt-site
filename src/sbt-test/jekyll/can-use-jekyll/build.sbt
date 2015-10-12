name := "test"

enablePlugins(JekyllPlugin)

siteSubdirName in Jekyll := "thisIsJekyll"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Jekyll).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("sbt")), s"Did not find expected content in:\n${content.mkString("\n")}")
}


