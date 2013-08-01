sbtPlugin := true

name := "sbt-site"

organization := "com.typesafe.sbt"

version := "0.7.0-SNAPSHOT"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

publishMavenStyle := false

publishTo <<= (version) { v =>
  def scalasbt(repo: String) = ("scalasbt " + repo, "http://repo.scala-sbt.org/scalasbt/sbt-plugin-" + repo)
  val (name, repo) = if (v.endsWith("-SNAPSHOT")) scalasbt("snapshots") else scalasbt("releases")
  Some(Resolver.url(name, url(repo))(Resolver.ivyStylePatterns))
}

libraryDependencies += "net.databinder" %% "unfiltered-jetty" % "0.6.8" 

site.settings

site.sphinxSupport()
