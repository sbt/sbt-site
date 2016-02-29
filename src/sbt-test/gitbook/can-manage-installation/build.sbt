name := "test"

enablePlugins(GitBookPlugin)

gitbookInstallDir in GitBook := Some(baseDirectory.value / "node_modules" / "gitbook")

TaskKey[Unit]("checkContent") := {
  val installDir = (gitbookInstallDir in GitBook).value
  val dest = (target in makeSite).value / (siteSubdirName in GitBook).value

  assert(installDir.isDefined, "gitbookInstallDir setting is not defined")

  val expectedFiles = Seq(
    installDir.get / "bin" / "gitbook",
    dest / "index.html"
  )

  for (file <- expectedFiles)
    assert(file.exists, s"${file.getAbsolutePath} did not exist")
}
