import sbtcompat.PluginCompat.{ *, given }

scalaVersion := "2.12.20"

Compile  / doc / scalacOptions ++= Seq(
  "-sourcepath", baseDirectory.value.toString,
  "-doc-source-url", {
    val branch = if (isSnapshot.value) "master" else s"v${version.value}"
    s"https://github.com/sbt/sbt-site/tree/${branch}/src/sbt-test/preprocess/transform-scaladoc€{FILE_PATH}.scala#L1"
  }
)

enablePlugins(PreprocessPlugin)

Preprocess / sourceDirectory := (Compile / doc / target).value
Preprocess / siteSubdirName := "api"
makeSite := Def.uncached(makeSite.dependsOn(Compile / doc).value)

//#preprocessRules
Preprocess / preprocessRules := Seq(
  ("\\.java\\.scala".r, _ => ".java")
)
//#preprocessRules

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (Preprocess / siteSubdirName).value
  val app = dest / "App.html"
  assert(app.exists, s"${app.getAbsolutePath} did not exist")
  val content = IO.readLines(app)
  assert(!content.exists(_.contains(".java.scala")), s"Found not replaced file suffix in:\n${content.mkString("\n")}")
}
