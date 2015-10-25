import java.util.Date

enablePlugins(PreprocessPlugin)

name := "preprocess test"

version := "0.0-ABCD"

preprocessVars := Map("VERSION" -> version.value, "DATE" -> new Date().toString)

siteSubdirName in Preprocess := "md-stuff"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Preprocess).value
  val readme = dest / "README.md"
  assert(readme.exists, s"${readme.getAbsolutePath} did not exist")
  val content = IO.readLines(readme)
  assert(content.exists(_.contains(version.value)), s"Did not find version in:\n${content.mkString("\n")}")
}
