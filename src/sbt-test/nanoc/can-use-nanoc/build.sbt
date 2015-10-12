name := "test"

enablePlugins(NanocPlugin)


siteSubdirName in Nanoc := "notJekyll"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Nanoc).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("nanoc site")), s"Did not find expected content in:\n${content.mkString("\n")}")
}

