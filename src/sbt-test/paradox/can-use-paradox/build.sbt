lazy val root = (project in file(".")).
  enablePlugins(ParadoxSitePlugin).
  settings(
    name := "test",
    siteSubdirName in Paradox := "docs",
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    TaskKey[Unit]("checkContent") := {
      val dest = (target in makeSite).value / (siteSubdirName in Paradox).value
      val index = dest / "index.html"
      assert(index.exists, s"${index.getAbsolutePath} did not exist")
      val content = IO.readLines(index)
      assert(content.exists(_.contains("Paradox Testing")), s"Did not find expected content in:\n${content.mkString("\n")}")

      val themeFile = dest / "paradox/theme/page.st"
      assert(!themeFile.exists, s"$themeFile should not exist [#80]")
    }
  )
