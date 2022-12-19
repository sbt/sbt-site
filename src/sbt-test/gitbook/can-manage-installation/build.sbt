name := "test"

enablePlugins(GitBookPlugin)

//#gitbookInstallDir
GitBook / gitbookInstallDir := Some(baseDirectory.value / "node_modules" / "gitbook")
//#gitbookInstallDir

TaskKey[Unit]("checkContent") := {
  val installDir = (GitBook / gitbookInstallDir).value
  val dest = (makeSite / target).value / (GitBook / siteSubdirName).value

  assert(installDir.isDefined, "gitbookInstallDir setting is not defined")

  val expectedFiles = Seq(
    installDir.get / "bin" / "gitbook",
    dest / "index.html"
  )

  for (file <- expectedFiles)
    assert(file.exists, s"${file.getAbsolutePath} did not exist")
}
