scalacOptions in (Compile, doc) ++= Seq(
  "-sourcepath", baseDirectory.value.toString,
  "-doc-source-url", {
    val branch = if (isSnapshot.value) "master" else s"v${version.value}"
    s"https://github.com/sbt/sbt-site/tree/${branch}/src/sbt-test/preprocess/transform-scaladoc€{FILE_PATH}.scala#L1"
  }
)

enablePlugins(PreprocessPlugin)

sourceDirectory in Preprocess := (target in (Compile, doc)).value
siteSubdirName in Preprocess := "api"
makeSite := makeSite.dependsOn(doc in Compile).value

//#preprocessRules
preprocessRules in Preprocess := Seq(
  ("\\.java\\.scala".r, _ => ".java")
)
//#preprocessRules

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Preprocess).value
  val app = dest / "App.html"
  assert(app.exists, s"${app.getAbsolutePath} did not exist")
  val content = IO.readLines(app)
  assert(!content.exists(_.contains(".java.scala")), s"Found not replaced file suffix in:\n${content.mkString("\n")}")
}
