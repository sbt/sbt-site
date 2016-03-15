name := "test"

version := "0.0-ABCD"

val someDirName = settingKey[String]("Some dir name")
someDirName := "someFancySource"

addMappingsToSiteDir(mappings in (Compile, packageSrc), someDirName)

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value
  val readme = dest / "README.html"
  assert(readme.exists, s"${readme.getAbsolutePath} did not exist")
  val content = IO.readLines(readme)
  assert(content.exists(_.contains(version.value)), s"Did not find version in:\n${content.mkString("\n")}")
  val fancy = dest / someDirName.value / "cats" / "preowned" / "Meow.scala"
  assert(fancy.exists, s"${fancy.getAbsolutePath} did not exist")
}
