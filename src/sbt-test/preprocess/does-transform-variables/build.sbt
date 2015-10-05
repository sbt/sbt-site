import java.util.Date

import com.typesafe.sbt.site.PreprocessSupport._

name := "test"

site.settings

version := "0.0-ABCD"

site.preprocessSite()

preprocessVars := Map("VERSION" -> version.value, "DATE" -> new Date().toString)

TaskKey[Unit]("checkContent") := {
  val dest = (target in Preprocess).value
  println((mappings in Preprocess).value)
  val readme = dest / "README.md"
  assert(readme.exists, s"${readme.getAbsolutePath} did not exist")
  val content = IO.readLines(readme)
  assert(content.exists(_.contains(version.value)), s"Did not find version in:\n${content.mkString("\n")}")
}
