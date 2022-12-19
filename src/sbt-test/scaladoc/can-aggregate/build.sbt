name := "scaladoc subproject test"

ThisBuild / scalaVersion := "2.12.17"

//#subprojects
lazy val cats = project.in(file("cats"))
lazy val kittens = project.in(file("kittens")).dependsOn(cats)

//#subprojects

//#unidoc-site
lazy val root = project.in(file("."))
  .settings(
    ScalaUnidoc / siteSubdirName := "api",
    addMappingsToSiteDir((ScalaUnidoc / packageDoc / mappings, ScalaUnidoc / siteSubdirName)
  )
  .enablePlugins(ScalaUnidocPlugin)
  .aggregate(cats, kittens)
//#unidoc-site
  .aggregate(siteWithScaladoc, siteWithScaladocAlt)

//#scaladoc-site
// Define a `Configuration` for each project.
val Cats = config("cats")
val Kittens = config("kittens")

lazy val siteWithScaladoc = project.in(file("site/scaladoc"))
  .settings(
    SiteScaladocPlugin.scaladocSettings(Cats, cats / Compile / packageDoc / mappings, "api/cats"),
    SiteScaladocPlugin.scaladocSettings(Kittens, kittens / Compile / packageDoc / mappings, "api/kittens")
  )
//#scaladoc-site

//#scaladoc-site-alternative
lazy val scaladocSiteProjects = List((cats, Cats), (kittens, Kittens))

lazy val scaladocSiteSettings = scaladocSiteProjects.flatMap { case (project, conf) =>
  SiteScaladocPlugin.scaladocSettings(
    conf,
    project / Compile / packageDoc / mappings,
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

  val unidocBase = (root / makeSite / target).value
  checkFileContent(unidocBase / "index.html", "Site with unidoc")
  checkFileContent(unidocBase / "api/index.html", "cats.Catnoid", "cats.LolCat", "kittens.Kitteh")

  val scaladocSites = Seq(
    (siteWithScaladoc / makeSite / target).value,
    (siteWithScaladocAlt / makeSite / target).value
  )

  for (scaladocSite <- scaladocSites) {
    checkFileContent(scaladocSite / "index.html", "Site with scaladoc")
    checkFileContent(scaladocSite / "api/cats/index.html", "cats.Catnoid", "cats.LolCat")
    checkFileContent(scaladocSite / "api/kittens/index.html", "kittens.Kitteh")
  }
}
