import java.util.Date

//#enablePlugin
enablePlugins(PreprocessPlugin)
//#enablePlugin

name := "preprocess test"

version := "0.0-ABCD"

//#preprocessVars
preprocessVars in Preprocess := Map("VERSION" -> version.value, "DATE" -> new Date().toString)
//#preprocessVars

preprocessRules in Preprocess := Seq(
  ("Author: ([a-z]+)".r, _.group(0).toUpperCase)
)

//#preprocessIncludeFilter
preprocessIncludeFilter := "*.md" | "*.markdown"
//#preprocessIncludeFilter

//#sourceDirectory
sourceDirectory in Preprocess := sourceDirectory.value / "site-preprocess"
//#sourceDirectory

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Preprocess).value
  val readme = dest / "README.md"
  assert(readme.exists, s"${readme.getAbsolutePath} did not exist")
  val content = IO.readLines(readme)
  assert(content.exists(_.contains(version.value)), s"Did not find version in:\n${content.mkString("\n")}")
  assert(content.exists(_.contains("AUTHOR: ME")), s"Did not find 'AUTHOR: ME' in: \n${content.mkString("\n")}")
}
