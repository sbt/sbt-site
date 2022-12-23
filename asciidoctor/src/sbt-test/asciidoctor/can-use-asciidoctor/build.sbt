name := "test"

//#enablePlugin
enablePlugins(AsciidoctorPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/asciimd`
Asciidoctor / siteSubdirName := "asciimd"
//#siteSubdirName

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (Asciidoctor / siteSubdirName).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val diagram = dest / "asciidoctor-diagram-process.png"
  assert(diagram.exists, s"${diagram.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("sbt")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
