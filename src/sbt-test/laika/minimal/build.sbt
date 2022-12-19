name := "test"

//#enablePlugin
enablePlugins(LaikaSitePlugin)
//#enablePlugin

LaikaSite / siteSubdirName := "minimal"

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (LaikaSite / siteSubdirName).value

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
