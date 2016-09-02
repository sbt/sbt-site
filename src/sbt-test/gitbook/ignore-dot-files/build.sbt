name := "test"

enablePlugins(GitBookPlugin)

sourceDirectory in GitBook := baseDirectory.value
siteSubdirName in GitBook := "docs"

val gitHeadPath = ".git/HEAD"

initialize := {
  // Create .git test file before running makeSite
  IO.write(baseDirectory.value / gitHeadPath, "test")
}

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in GitBook).value

  val generatedGitFile = baseDirectory.value / gitHeadPath
  assert(generatedGitFile.exists, s"${generatedGitFile.getAbsolutePath} did not exist")

  for (path <- Seq("index.html")) {
    val file = dest / path
    assert(file.exists, s"${file.getAbsolutePath} did not exist")
  }

  for (path <- Seq(gitHeadPath, ".gitignore", ".bookignore", "target", "project")) {
    val file = dest / path
    assert(!file.exists, s"${file.getAbsolutePath} should not exist")
  }
}
