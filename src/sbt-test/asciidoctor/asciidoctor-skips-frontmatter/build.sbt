name := "test"

//#enablePlugin
enablePlugins(AsciidoctorPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/asciimd`
Asciidoctor / siteSubdirName := "asciimd"
Asciidoctor / asciidoctorAttributes := Map(
  "skip-front-matter" -> "", // skip-front-matter is a flag that just has to be present.
  "lang" -> "nl")
//#siteSubdirName

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (Asciidoctor / siteSubdirName).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)

  assert(content.exists(_.contains("lang=\"nl\"")), s"Did not find expected content in:\n${content.mkString("\n")}")
  assert(content.forall(!_.contains("title: \"Document Title\"")), s"Expected front-matter metadata to be removed from the output in:\n${content.mkString("\n")}")
}
