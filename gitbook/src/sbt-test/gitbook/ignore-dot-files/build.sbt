name := "test"

//#enablePlugin
enablePlugins(GitBookPlugin)
//#enablePlugin

GitBook / sourceDirectory := baseDirectory.value

//#siteSubdirName
// Puts output in `target/site/book`
GitBook / siteSubdirName := "book"
//#siteSubdirName

val gitHeadPath = ".git/HEAD"

initialize := {
  // Create .git test file before running makeSite
  IO.write(baseDirectory.value / gitHeadPath, "test")
}

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (GitBook / siteSubdirName).value

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
