import com.typesafe.sbt.SbtGit._

versionWithGit

scalaVersion := "2.10.4"

git.baseVersion := "0.7"

sbtPlugin := true

sbtVersion in Global := {
  scalaBinaryVersion.value match {
    case "2.10" => "0.13.0"
    case "2.9.2" => "0.12.4"
  }
}

scalaVersion in Global := "2.9.2"

crossScalaVersions in Global := Seq("2.9.2", "2.10.4")

crossScalaVersions := Seq("2.9.2", "2.10.4")

name := "sbt-site"

organization := "com.typesafe.sbt"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

publishMavenStyle := false

publishTo <<= (version) { v =>
  def scalasbt(repo: String) = ("scalasbt " + repo, "http://repo.scala-sbt.org/scalasbt/sbt-plugin-" + repo)
  val (name, repo) = if (v.endsWith("-SNAPSHOT")) scalasbt("snapshots") else scalasbt("releases")
  Some(Resolver.url(name, url(repo))(Resolver.ivyStylePatterns))
}

libraryDependencies += "net.databinder" %% "unfiltered-jetty" % "0.6.8" 

libraryDependencies += "net.databinder" %% "pamflet-library" % "0.5.0"

site.settings

site.sphinxSupport()

scriptedSettings

scriptedLaunchOpts += "-Dproject.version="+version.value

sbtVersion in Global := {
  scalaBinaryVersion.value match {
    case "2.10" => "0.13.0"
    case "2.9.2" => "0.12.4"
  }
}

scalacOptions += "-deprecation"
