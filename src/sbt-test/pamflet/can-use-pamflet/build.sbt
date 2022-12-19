name := "test"

//#enablePlugin
enablePlugins(PamfletPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/parchment`
Pamflet / siteSubdirName := "parchment"
//#siteSubdirName

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (Pamflet / siteSubdirName).value
  val index = dest / "Pamphlet+Testing.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("Phun")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
