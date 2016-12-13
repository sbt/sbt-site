name := "test"

version := "0.0-ABCD"

//#mappings
mappings in makeSite ++= Seq(
  file("LICENSE") -> "LICENSE",
  file("src/assets/favicon.ico") -> "favicon.ico"
)
//#mappings

//#addMappingsToSiteDir
val someDirName = settingKey[String]("Some dir name")
someDirName := "someFancySource"

addMappingsToSiteDir(mappings in (Compile, packageSrc), someDirName)
//#addMappingsToSiteDir

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value
  val readme = dest / "README.html"
  assert(readme.exists, s"${readme.getAbsolutePath} did not exist")
  val content = IO.readLines(readme)
  assert(content.exists(_.contains(version.value)), s"Did not find version in:\n${content.mkString("\n")}")
  val license = dest / "LICENSE"
  assert(license.exists, s"${license.getAbsolutePath} did not exist")
  val favicon = dest / "favicon.ico"
  assert(favicon.exists, s"${favicon.getAbsolutePath} did not exist")
  val fancy = dest / someDirName.value / "cats" / "preowned" / "Meow.scala"
  assert(fancy.exists, s"${fancy.getAbsolutePath} did not exist")
}
