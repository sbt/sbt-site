name := "test"

//#enablePlugin
enablePlugins(PamfletPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/parchment`
siteSubdirName in Pamflet := "parchment"
//#siteSubdirName

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Pamflet).value
  val index = dest / "Pamphlet+Testing.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("Phun")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
