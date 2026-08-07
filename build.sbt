def scala212 = "2.12.20"
def scala3 = "3.8.4"

inThisBuild(Seq(
  organization := "com.github.sbt",
  organizationName := "sbt",
  organizationHomepage := Some(url("https://www.scala-sbt.org/")),

  crossScalaVersions := Seq(scala212, scala3),
  scalaVersion := scala3,

  homepage := Some(url("https://www.scala-sbt.org/sbt-site/")),
  licenses += ("BSD 3-Clause", url("https://opensource.org/licenses/BSD-3-Clause")),
  //#scm-info
  scmInfo := Some(ScmInfo(url("https://github.com/sbt/sbt-site"), "scm:git:git@github.com:sbt/sbt-site.git")),
  //#scm-info
  developers += Developer(
    "contributors",
    "Contributors",
    "https://gitter.im/sbt/sbt-site",
    url("https://github.com/sbt/sbt-site/graphs/contributors")
  ),
))

val pluginSettings = Seq(
  sbtPlugin := true,
  scriptedLaunchOpts += "-Dproject.version=" + version.value,
  addSbtPlugin("com.github.sbt" % "sbt2-compat" % "0.2.0"),
  // scriptedBufferLog := false
  (pluginCrossBuild / sbtVersion) := {
    scalaBinaryVersion.value match {
      case "2.12" => "1.9.7"
      case _      => "2.0.6"
    }
  },
  scriptedSbt := {
    scalaBinaryVersion.value match {
      case "2.12" => "1.12.15"
      case _      => (pluginCrossBuild / sbtVersion).value
    }
  },
)

val commonSettings = Seq(
  scalacOptions ++= {
    scalaBinaryVersion.value match {
      case "2.12" =>
        Seq(
          "-deprecation",
          "-unchecked",
          "-encoding",
          "UTF-8",
          "-Xsource:3",
          "-release",
          "8"
        )
      case _ => Nil
    }
  }
)

val unfilteredVersion = "0.12.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "sbt-site-root",
    publish / skip := true,
    Compile / publishArtifact := false,
    crossScalaVersions := Nil,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))),
    Compile / paradoxMaterialTheme ~= {
      _.withFavicon("img/favicon.png")
        .withLogo("img/sbt-logo.svg")
        .withRepository(uri("https://github.com/sbt/sbt-site"))
    }
  )
  .aggregate(core, asciidoctor, gitbook, paradox, sphinx)
  .enablePlugins(SitePreviewPlugin, ParadoxSitePlugin, ParadoxMaterialThemePlugin)

lazy val core = project
  .in(file("core"))
  .enablePlugins(SbtPlugin)
  .settings(pluginSettings)
  .settings(commonSettings)
  .settings(
    name := "sbt-site",
    libraryDependencies ++= Seq(
      "ws.unfiltered" %% "unfiltered-directives" % unfilteredVersion,
      "ws.unfiltered" %% "unfiltered-filter" % unfilteredVersion,
      "ws.unfiltered" %% "unfiltered-jetty" % unfilteredVersion,
      "ws.unfiltered" %% "unfiltered-specs2" % unfilteredVersion % Test,
    )
  )

lazy val asciidoctor = project
  .in(file("asciidoctor"))
  .settings(
    name := "sbt-site-asciidoctor",
    libraryDependencies ++= Seq(
      "org.asciidoctor" % "asciidoctorj" % "2.5.13",
      "org.asciidoctor" % "asciidoctorj-diagram" % "1.5.18"
    )
  )
  .dependsOn(core)
  .enablePlugins(SbtPlugin)
  .settings(pluginSettings)
  .settings(commonSettings)

lazy val gitbook = project
  .in(file("gitbook"))
  .settings(
    name := "sbt-site-gitbook",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.9"
    )
  )
  .dependsOn(core)
  .enablePlugins(SbtPlugin)
  .settings(pluginSettings)
  .settings(commonSettings)

// https://github.com/sbt/sbt-site/issues/182
// lazy val hugo = project...

// https://github.com/sbt/sbt-site/issues/183
// lazy val jekyll = project...

// https://github.com/sbt/sbt-site/issues/204
// lazy val laika = project...
//libraryDependencies +=
//  Defaults.sbtPluginExtra(
//    "org.planet42" % "laika-sbt" % "0.8.0",
//    (pluginCrossBuild / sbtBinaryVersion).value,
//    (pluginCrossBuild / scalaBinaryVersion).value
//  )

// https://github.com/sbt/sbt-site/issues/205
// lazy val nanoc = project...

// https://github.com/sbt/sbt-site/issues/206
// lazy val pamflet = project...
//val pamfletDependencies = Seq(
//  "org.foundweekends" %% "pamflet-library" % "0.10.0",
//  "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
//  "org.yaml" % "snakeyaml" % "1.33",
//)

lazy val paradox = project
  .in(file("paradox"))
  .dependsOn(core)
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-site-paradox",
    addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.11.0-M4"),
    pluginSettings,
    commonSettings,
  )

lazy val sphinx = project
  .in(file("sphinx"))
  .settings(
    name := "sbt-site-sphinx",
  )
  .dependsOn(core)
  .enablePlugins(SbtPlugin)
  .settings(pluginSettings)
  .settings(commonSettings)

//#ghpages-publish
enablePlugins(GhpagesPlugin)
git.remoteRepo := scmInfo.value.get.connection.replace("scm:git:", "")
//#ghpages-publish
