addSbtPlugin("com.github.sbt" % "sbt-site" % sys.props("project.version"))

resolvers += "jgit-repo" at "https://download.eclipse.org/jgit/maven"

addSbtPlugin(
  ("com.github.sbt" % "sbt-ghpages" % "0.7.0")
    // sbt-ghpages depends on sbt-site 1.4.1, which pulls Scala XML 1.x
    .exclude("org.scala-lang.modules", "scala-xml_2.12")
)
