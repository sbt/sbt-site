import com.typesafe.sbt.SbtGit._
import bintray.Keys._

bintrayPublishSettings

publishMavenStyle := false

bintrayOrganization in bintray := Some("sbt")

name in bintray := "sbt-site"

repository in bintray := "sbt-plugin-releases"

licenses += ("BSD 3-Clause", url("http://opensource.org/licenses/BSD-3-Clause"))

sbtPlugin := true

name := "sbt-site"

organization := "com.typesafe.sbt"

scalaVersion := "2.10.4"

versionWithGit

git.baseVersion := "1.0"

scalaVersion in Global := "2.10.4"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-jetty" % "0.6.8",
  "net.databinder" %% "pamflet-library"  % "0.6.0",
  "org.yaml"        % "snakeyaml"        % "1.13",
  "org.asciidoctor" % "asciidoctorj"     % "1.5.2"
)

site.settings

site.sphinxSupport()

scriptedSettings

scriptedLaunchOpts += "-Dproject.version="+version.value

scalacOptions ++= Seq("-deprecation", "-unchecked")
