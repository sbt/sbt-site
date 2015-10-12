name := "test"

enablePlugins(SphinxPlugin)

siteSubdirName in Sphinx := "madewithpython"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Sphinx).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("SBT")), s"Did not find expected content in:\n${content.mkString("\n")}")
}

