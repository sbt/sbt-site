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
