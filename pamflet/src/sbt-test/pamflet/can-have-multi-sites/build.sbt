name := "test"

val Site1 = config("site1")

val Site2 = config("site2")

PamfletPlugin.pamfletSettings(Site1)

Site1 / sourceDirectory := sourceDirectory.value / "pamflet-site-1"

Site1 / siteSubdirName := "chapter1"

PamfletPlugin.pamfletSettings(Site2)

Site2 / sourceDirectory := sourceDirectory.value / "pamflet-site-2"

Site2 / siteSubdirName := "chapter2"

TaskKey[Unit]("checkContent") := {
  val dest1 = (makeSite / target).value / (Site1 / siteSubdirName).value
  val dest2 = (makeSite / target).value / (Site2 / siteSubdirName).value
  for ((dest, i) ← Seq(dest1, dest2).zipWithIndex) {
    val index = dest / s"Pamphlet+Testing+${i+1}.html"
    assert(index.exists, s"${index.getAbsolutePath} did not exist")
    val content = IO.readLines(index)
    assert(
      content.exists(_.contains(s"Chapter ${i+1}")),
      s"Did not find expected content in:\n${content.mkString("\n")}")
  }
}
