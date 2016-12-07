lazy val root = (project in file(".")).
  enablePlugins(ParadoxSitePlugin).
  settings(
    name := "test",
    siteSubdirName in Paradox := "docs",
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    paradoxProperties in Paradox ++= Map(
      "extref.rfc.base_url" -> "http://tools.ietf.org/html/rfc%s"
    ),
    TaskKey[Unit]("checkContent") := {
      val dest = (target in makeSite).value / (siteSubdirName in Paradox).value
      val index = dest / "index.html"
      assert(index.exists, s"${index.getAbsolutePath} did not exist")
      val content = IO.readLines(index)
      for (text <- Seq("Paradox Testing", "http://tools.ietf.org/html/rfc2119"))
        assert(content.exists(_.contains(text)), s"Did not find '$text' in:\n${content.mkString("\n")}")

      val themeFile = dest / "paradox/theme/page.st"
      assert(!themeFile.exists, s"$themeFile should not exist [#80]")
    }
  )
