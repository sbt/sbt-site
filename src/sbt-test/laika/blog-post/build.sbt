name := "test"

//#enablePlugin
enablePlugins(LaikaSitePlugin)
//#enablePlugin

LaikaSite / sourceDirectory := sourceDirectory.value / "blog"

LaikaSite / siteSubdirName := "blog"

//#laikaSbtPluginCustomization
LaikaSite / laikaBlockDirectives += CustomDirectives.postsToc
LaikaSite / laikaSiteRenderers += CustomDirectives.postsRenderer
//#laikaSbtPluginCustomization

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (LaikaSite / siteSubdirName).value

  val expectedFilesAndWords = Map(
    dest / "index.html" -> Seq("Start Bootstrap Template", "Blog Post Title", "Second Post Title"),
    dest / "topics" / "post-1.html" -> Seq("Blog Post Title", "recusandae laborum minus inventore?"),
    dest / "topics" / "post-2.html" -> Seq("Second Post Title", "perspiciatis. Enim, iure!")
  )

  for ((file, words) <- expectedFilesAndWords) {
    assert(file.exists, s"${file.getAbsolutePath} did not exist")
    val content = IO.readLines(file)
    for (word <- words)
      assert(content.exists(_.contains(word)), s"Did not find $word content in:\n${content.mkString("\n")}")
  }
}
