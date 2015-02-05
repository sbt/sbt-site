import com.typesafe.sbt.SbtGit._

sbtPlugin := true

name := "sbt-site"

organization := "com.typesafe.sbt"

scalaVersion := "2.10.4"

//versionWithGit
version := "0.8.2-SNAPSHOT"

git.baseVersion := "1.0"

sbtVersion in Global := {
  scalaBinaryVersion.value match {
    case "2.10" => "0.13.0"
    case "2.9.2" => "0.12.4"
  }
}

scalaVersion in Global := "2.9.2"

crossScalaVersions in Global := Seq("2.9.2", "2.10.4")

crossScalaVersions := Seq("2.9.2", "2.10.4")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

publishMavenStyle := false

publishTo := {
  if (isSnapshot.value) Some(Classpaths.sbtPluginSnapshots)
  else Some(Classpaths.sbtPluginReleases)
}

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
