name := "test"

//#tut
enablePlugins(ParadoxPlugin, ParadoxSitePlugin, TutPlugin)
Compile / paradox / sourceDirectory := tutTargetDirectory.value
makeSite := makeSite.dependsOn(tut).value
//#tut

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value

  def checkFileContent(file: File, expected: String) = {
    assert(file.exists, s"${file.getAbsolutePath} did not exist")
    val actual = IO.readLines(file)
    assert(actual.exists(_.contains(expected)), s"Did not find $expected in:\n${actual.mkString("\n")}")
  }

  checkFileContent(dest / "index.html", "Here is how you add numbers")
  checkFileContent(dest / "test.html", "scala.util.Try")
}
