sbtPlugin := true

name := "sbt-site"

organization := "com.typesafe.sbt"

version := "0.7.0-SNAPSHOT"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

libraryDependencies += "net.databinder" % "pamflet-library_2.9.1" % "0.4.1"

libraryDependencies += "com.tristanhunt" % "knockoff_2.9.1" % "0.8.0-16"

publishMavenStyle := false

publishTo <<= (version) { v =>
  def scalasbt(repo: String) = ("scalasbt " + repo, "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-" + repo)
  val (name, repo) = if (v.endsWith("-SNAPSHOT")) scalasbt("snapshots") else scalasbt("releases")
  Some(Resolver.url(name, url(repo))(Resolver.ivyStylePatterns))
}