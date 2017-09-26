import scala.sys.process._

sbtPlugin := true

name := "sbt-site"

organization := "com.typesafe.sbt"

version := "1.3.2-SNAPSHOT"
crossSbtVersions := List("0.13.16", "1.0.2")

licenses += ("BSD 3-Clause", url("http://opensource.org/licenses/BSD-3-Clause"))
//#scm-info
scmInfo := Some(ScmInfo(url("https://github.com/sbt/sbt-site"), "git@github.com:sbt/sbt-site.git"))
//#scm-info

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers += Resolver.sonatypeRepo("releases")

val unfilteredVersion = "0.9.1"

libraryDependencies ++= Seq(
  "ws.unfiltered"  %% "unfiltered-directives" % unfilteredVersion,
  "ws.unfiltered"  %% "unfiltered-filter" % unfilteredVersion,
  "ws.unfiltered"  %% "unfiltered-jetty" % unfilteredVersion,
  "ws.unfiltered"  %% "unfiltered-specs2" % unfilteredVersion % "test",
  "org.foundweekends" %% "pamflet-library" % "0.7.1",
  "org.yaml"        % "snakeyaml"        % "1.13",
  "com.typesafe"    % "config"           % "1.2.1", // Last version to support Java 1.6
  "org.asciidoctor" % "asciidoctorj"     % "1.5.4"
)

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.3.0")

libraryDependencies ++= {
  if ((sbtBinaryVersion in pluginCrossBuild).value == "0.13") {
    Seq(
      Defaults.sbtPluginExtra(
        "org.planet42" % "laika-sbt" % "0.7.0",
        (sbtBinaryVersion in pluginCrossBuild).value,
        (scalaBinaryVersion in pluginCrossBuild).value
      )
    )
  } else Nil
}

// fixed in https://github.com/sbt/sbt/pull/3397 (for sbt 0.13.17)
sbtBinaryVersion in update := (sbtBinaryVersion in pluginCrossBuild).value

enablePlugins(ParadoxSitePlugin)
sourceDirectory in Paradox := sourceDirectory.value / "main" / "paradox"
paradoxTheme := Some(builtinParadoxTheme("generic"))
version in Paradox := {
  if (isSnapshot.value) "git tag -l".!!.split("\r?\n").last.substring(1)
  else version.value
}

//#ghpages-publish
enablePlugins(GhpagesPlugin)
git.remoteRepo := scmInfo.value.get.connection
//#ghpages-publish

TaskKey[Unit]("runScriptedTest") := Def.taskDyn {
  val sbtBinVersion = (sbtBinaryVersion in pluginCrossBuild).value
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
