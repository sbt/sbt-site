
sbtPlugin := true

name := "sbt-site"

organization := "com.typesafe.sbt"

version := "1.3.1-SNAPSHOT"

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

libraryDependencies ++= {
  if ((sbtVersion in pluginCrossBuild).value.startsWith("0.13")) {
    Seq(
      Defaults.sbtPluginExtra(
        "com.lightbend.paradox" % "sbt-paradox" % "0.2.13",
        (sbtBinaryVersion in pluginCrossBuild).value,
        (scalaBinaryVersion in pluginCrossBuild).value
      ),
      Defaults.sbtPluginExtra(
        "org.planet42" % "laika-sbt" % "0.7.0",
        (sbtBinaryVersion in pluginCrossBuild).value,
        (scalaBinaryVersion in pluginCrossBuild).value
      )
    )
  } else Nil
}

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

scriptedSettings

TaskKey[Unit]("runScriptedTest") := Def.taskDyn {
  if ((sbtVersion in pluginCrossBuild).value.startsWith("0.13")) {
    Def.task{
      scripted.toTask("").value
    }
  } else {
    val base = sbtTestDirectory.value
    val exclude = Seq(
      base / "paradox" * AllPassFilter, // paradox does not support sbt 1.0
      base / "laika" * AllPassFilter, // https://github.com/planet42/Laika/issues/57
      base / "site" * ("can-run-generator-twice" || "plays-nice-with-tut") // use paradox
    )

    val allTests = base * AllPassFilter * AllPassFilter filter { _.isDirectory }
    val tests = allTests --- exclude.reduce(_ +++ _)
    val args = tests.get.map(Path.relativeTo(base)).flatten.mkString(" ", " ", "")
    streams.value.log.info("scripted test args = " + args)

    Def.task{
      scripted.toTask(args).value
    }
  }
}.value

scriptedLaunchOpts += "-Dproject.version="+version.value

// scriptedBufferLog := false
