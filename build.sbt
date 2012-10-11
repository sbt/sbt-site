sbtPlugin := true

// This should be tied to sbtPlugin IMHO.
publishMavenStyle := false

name := "sbt-site-plugin"

organization := "com.jsuereth"

version := "0.6.0-SNAPSHOT"

publishTo := Some(Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns))

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

libraryDependencies += "net.databinder" % "pamflet-library_2.9.1" % "0.4.1"

libraryDependencies += "com.tristanhunt" % "knockoff_2.9.1" % "0.8.0-16"
