name := "test"

enablePlugins(LaikaSitePlugin)

siteSubdirName in LaikaSite := "minimal"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in LaikaSite).value

  val expectedFilesAndWords = Map(
    dest / "index.html" -> Seq("test", "Minimal")
  )

  for ((file, words) <- expectedFilesAndWords) {
    assert(file.exists, s"${file.getAbsolutePath} did not exist")
    val content = IO.readLines(file)
    for (word <- words)
      assert(content.exists(_.contains(word)), s"Did not find $word content in:\n${content.mkString("\n")}")
  }
}
