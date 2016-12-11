name := "test"

val checkContent = taskKey[Unit]("checkContent")

val v2 = project.configure(gitbookProject("v2"))
val v2Output = project.configure(gitbookProject("v2-output"))
val v3 = project.configure(gitbookProject("v3"))

val root = project.in(file("."))
  .aggregate(v2, v2Output, v3)

def gitbookProject(version: String)(project: Project): Project = project
  .in(file(version))
  .enablePlugins(GitBookPlugin)
  .settings(
    checkContentTask,
    siteSubdirName in GitBook := "docs"
  )

def checkContentTask = checkContent := {
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
