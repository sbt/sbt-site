name := "scaladoc subproject test"

//#subprojects
lazy val cats = project.in(file("cats"))
lazy val kittens = project.in(file("kittens")).dependsOn(cats)

//#subprojects

//#unidoc-site
lazy val root = project.in(file("."))
  .settings(
    unidocSettings,
    siteSubdirName in ScalaUnidoc := "api",
    addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), siteSubdirName in ScalaUnidoc)
  )
  .aggregate(cats, kittens)
//#unidoc-site
  .aggregate(siteWithScaladoc, siteWithScaladocAlt)

//#scaladoc-site
// Define a `Configuration` for each project.
val Cats = config("cats")
val Kittens = config("kittens")

lazy val siteWithScaladoc = project.in(file("site/scaladoc"))
  .settings(
    SiteScaladocPlugin.scaladocSettings(Cats, mappings in (Compile, packageDoc) in cats, "api/cats"),
    SiteScaladocPlugin.scaladocSettings(Kittens, mappings in (Compile, packageDoc) in kittens, "api/kittens")
  )
//#scaladoc-site

//#scaladoc-site-alternative
lazy val scaladocSiteProjects = List(cats, kittens)

lazy val scaladocSiteSettings = scaladocSiteProjects.flatMap { project =>
  // Create a config for setting the subdirectory
  SiteScaladocPlugin.scaladocSettings(
    config(project.id),
    mappings in (Compile, packageDoc) in project,
    s"api/${project.id}"
  )
}

val siteWithScaladocAlt = project.in(file("site/scaladoc-alternative"))
  .settings(scaladocSiteSettings)
//#scaladoc-site-alternative

TaskKey[Unit]("checkContent") := {
  def checkFileContent(file: File, expected: String*) = {
    assert(file.exists, s"${file.getAbsolutePath} did not exist")
    val actual = IO.readLines(file)
    expected.foreach { text =>
      assert(actual.exists(_.contains(text)), s"Did not find $text in:\n${actual.mkString("\n")}")
    }
  }

  val unidocBase = (target in makeSite in root).value
  checkFileContent(unidocBase / "index.html", "Site with unidoc")
  checkFileContent(unidocBase / "api/index.html", "cats.Catnoid", "cats.LolCat", "kittens.Kitteh")

  val scaladocSites = Seq(
    (target in makeSite in siteWithScaladoc).value,
    (target in makeSite in siteWithScaladocAlt).value
  )

  for (scaladocSite <- scaladocSites) {
    checkFileContent(scaladocSite / "index.html", "Site with scaladoc")
    checkFileContent(scaladocSite / "api/cats/index.html", "cats.Catnoid", "cats.LolCat")
    checkFileContent(scaladocSite / "api/kittens/index.html", "kittens.Kitteh")
  }
}
