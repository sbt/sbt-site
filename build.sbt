import scala.sys.process._

sbtPlugin := true
enablePlugins(SbtPlugin)

name := "sbt-site"

organization := "com.typesafe.sbt"
organizationName := "sbt"
organizationHomepage := Some(url("https://www.scala-sbt.org/"))

homepage := Some(url("https://www.scala-sbt.org/sbt-site/"))

version := "1.4.0"
crossSbtVersions := List("1.1.6")

licenses += ("BSD 3-Clause", url("https://opensource.org/licenses/BSD-3-Clause"))
//#scm-info
scmInfo := Some(ScmInfo(url("https://github.com/sbt/sbt-site"), "scm:git:git@github.com:sbt/sbt-site.git"))
//#scm-info

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers += Resolver.sonatypeRepo("releases")

val unfilteredVersion = "0.9.1"

libraryDependencies ++= Seq(
  "ws.unfiltered"  %% "unfiltered-directives" % unfilteredVersion,
  "ws.unfiltered"  %% "unfiltered-filter" % unfilteredVersion,
  "ws.unfiltered"  %% "unfiltered-jetty" % unfilteredVersion,
  "ws.unfiltered"  %% "unfiltered-specs2" % unfilteredVersion % "test",
  "org.foundweekends" %% "pamflet-library" % "0.8.0",
  "org.yaml"        % "snakeyaml"        % "1.24",
  "com.typesafe"    % "config"           % "1.3.4",
  "org.asciidoctor" % "asciidoctorj"     % "1.6.2",
  "org.asciidoctor" % "asciidoctorj-diagram" % "1.5.16"
)

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.5.5")

libraryDependencies +=
  Defaults.sbtPluginExtra(
    "org.planet42" % "laika-sbt" % "0.8.0",
    (sbtBinaryVersion in pluginCrossBuild).value,
    (scalaBinaryVersion in pluginCrossBuild).value
  )

enablePlugins(ParadoxSitePlugin, ParadoxMaterialThemePlugin)
Paradox / sourceDirectory := sourceDirectory.value / "main" / "paradox"
ParadoxMaterialThemePlugin.paradoxMaterialThemeSettings(Paradox)
Paradox / paradoxMaterialTheme ~= {
  _.withFavicon("img/favicon.png")
   .withLogo("img/sbt-logo.svg")
   .withRepository(uri("https://github.com/sbt/sbt-site"))
}
Paradox / version := {
  if (isSnapshot.value) "git tag -l".!!.split("\r?\n").last.substring(1)
  else version.value
}

//#ghpages-publish
enablePlugins(GhpagesPlugin)
git.remoteRepo := scmInfo.value.get.connection.replace("scm:git:", "")
//#ghpages-publish

TaskKey[Unit]("runScriptedTest") := Def.taskDyn {
  val sbtBinVersion = (pluginCrossBuild / sbtBinaryVersion).value
  val base = sbtTestDirectory.value

  def isCompatible(directory: File): Boolean = {
    val buildProps = new java.util.Properties()
    IO.load(buildProps, directory / "project" / "build.properties")
    Option(buildProps.getProperty("sbt.version"))
      .map { version =>
        val requiredBinVersion = CrossVersion.binarySbtVersion(version)
        val compatible = requiredBinVersion == sbtBinVersion
        if (!compatible) {
          val testName = directory.relativeTo(base).getOrElse(directory)
          streams.value.log.warn(s"Skipping $testName since it requires sbt $requiredBinVersion")
        }
        compatible
      }
      .getOrElse(true)
  }

  val testDirectoryFinder = base * AllPassFilter * AllPassFilter filter { _.isDirectory }
  val tests = for {
    test <- testDirectoryFinder.get
    if isCompatible(test)
    path <- Path.relativeTo(base)(test)
  } yield path.replace('\\', '/')

  if (tests.nonEmpty)
    Def.task(scripted.toTask(tests.mkString(" ", " ", "")).value)
  else
    Def.task(streams.value.log.warn("No tests can be run for this sbt version"))
}.value

scriptedLaunchOpts += "-Dproject.version="+version.value

// scriptedBufferLog := false
