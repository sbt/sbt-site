name := "test"

enablePlugins(GitBookPlugin)

siteSubdirName in GitBook := "notJekyll"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in GitBook).value

  val expectedFilesAndWords = Map(
    dest / "index.html" -> Seq("SBT", "GitBook", "User"),
    dest / "chapter1" / "index.html" -> Seq("First", "Chapter")
  )

  for ((file, words) <- expectedFilesAndWords) {
    assert(file.exists, s"${file.getAbsolutePath} did not exist")
    val content = IO.readLines(file)
    for (word <- words)
      assert(content.exists(_.contains(word)), s"Did not find $word content in:\n${content.mkString("\n")}")
  }
}
