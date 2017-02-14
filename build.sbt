
sbtPlugin := true

name := "sbt-site"

organization := "com.typesafe.sbt"

version := "1.3.0-SNAPSHOT"

licenses += ("BSD 3-Clause", url("http://opensource.org/licenses/BSD-3-Clause"))
//#scm-info
scmInfo := Some(ScmInfo(url("https://github.com/sbt/sbt-site"), "git@github.com:sbt/sbt-site.git"))
//#scm-info

scalaVersion := "2.10.6"

scalaVersion in Global := "2.10.6"

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-directives" % "0.8.4",
  "net.databinder" %% "unfiltered-filter" % "0.8.4",
  "net.databinder" %% "unfiltered-jetty" % "0.8.4",
  "net.databinder" %% "unfiltered-specs2" % "0.8.4" % "test",
  "net.databinder" %% "pamflet-library"  % "0.6.0",
  "org.yaml"        % "snakeyaml"        % "1.13",
  "com.typesafe"    % "config"           % "1.2.1", // Last version to support Java 1.6
  "org.asciidoctor" % "asciidoctorj"     % "1.5.4"
)

addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.2.9")

enablePlugins(ParadoxSitePlugin)
sourceDirectory in Paradox := sourceDirectory.value / "main" / "paradox"
paradoxTheme := Some(builtinParadoxTheme("generic"))
version in Paradox := {
  if (isSnapshot.value) "git tag -l".!!.split("\r?\n").last.substring(1)
  else version.value
}

//#ghpages-publish
ghpages.settings
git.remoteRepo := scmInfo.value.get.connection
//#ghpages-publish

scriptedSettings

scriptedLaunchOpts += "-Dproject.version="+version.value

// scriptedBufferLog := false
