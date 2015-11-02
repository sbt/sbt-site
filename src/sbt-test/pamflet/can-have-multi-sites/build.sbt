name := "test"

val Site1 = config("site1")

val Site2 = config("site2")

PamfletPlugin.pamfletSettings(Site1)

sourceDirectory in Site1 := sourceDirectory.value / "pamflet-site-1"

siteSubdirName in Site1 := "chapter1"

PamfletPlugin.pamfletSettings(Site2)

sourceDirectory in Site2 := sourceDirectory.value / "pamflet-site-2"

siteSubdirName in Site2 := "chapter2"

TaskKey[Unit]("checkContent") := {
  val dest1 = (target in makeSite).value / (siteSubdirName in Site1).value
  val dest2 = (target in makeSite).value / (siteSubdirName in Site2).value
  for ((dest, i) ← Seq(dest1, dest2).zipWithIndex) {
    val index = dest / s"Pamphlet+Testing+${i+1}.html"
    assert(index.exists, s"${index.getAbsolutePath} did not exist")
    val content = IO.readLines(index)
    assert(
      content.exists(_.contains(s"Chapter ${i+1}")),
      s"Did not find expected content in:\n${content.mkString("\n")}")
  }
}
