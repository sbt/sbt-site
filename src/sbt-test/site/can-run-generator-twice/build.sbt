name := "test"

//#advanced-usage
// Define two `Configuration` instances.
val Site1 = config("site1")
val Site2 = config("site2")

// Apply the default Paradox settings to the `Site1` config
ParadoxSitePlugin.paradoxSettings(Site1)

// Customize the source directory
sourceDirectory in Site1 := sourceDirectory.value / "paradox-site-1"

// Customize the output subdirectory
siteSubdirName in Site1 := "chapter1"

// Same as above, but for config `Site2`
ParadoxSitePlugin.paradoxSettings(Site2)
sourceDirectory in Site2 := sourceDirectory.value / "paradox-site-2"
siteSubdirName in Site2 := "chapter2"

// Global Paradox settings
enablePlugins(ParadoxPlugin)
paradoxTheme := Some(builtinParadoxTheme("generic"))
//#advanced-usage

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value
  for (i <- 1 to 2) {
    val chapter = dest / s"chapter$i" / "index.html"
    assert(chapter.exists, s"${chapter.getAbsolutePath} did not exist")
    val content = IO.readLines(chapter)
    for (text <- Seq(s"Chapter $i", "It was a stormy night"))
      assert(content.exists(_.contains(text)), s"Did not find '$text' in:\n${content.mkString("\n")}")
  }
}
