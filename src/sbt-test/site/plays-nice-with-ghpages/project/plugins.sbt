addSbtPlugin("com.github.sbt" % "sbt-site" % sys.props("project.version"))

resolvers += "jgit-repo" at "https://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.2")
