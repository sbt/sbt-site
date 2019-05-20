name := "test"

//#enablePlugin
enablePlugins(ParadoxSitePlugin)
//#enablePlugin

siteSubdirName in Paradox := "docs"

//#paradoxTheme
paradoxTheme := Some(builtinParadoxTheme("generic"))
//#paradoxTheme

//#paradoxProperties
paradoxProperties ++= Map(
  "extref.rfc.base_url" -> "https://tools.ietf.org/html/rfc%s"
)
//#paradoxProperties

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Paradox).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  for (text <- Seq("Paradox Testing", "https://tools.ietf.org/html/rfc2119"))
    assert(content.exists(_.contains(text)), s"Did not find '$text' in:\n${content.mkString("\n")}")

  val themeFile = dest / "paradox/theme/page.st"
  assert(!themeFile.exists, s"$themeFile should not exist [#80]")
}
