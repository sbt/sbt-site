addSbtPlugin("com.github.sbt" % "sbt-site" % sys.props("project.version"))

resolvers += "jgit-repo" at "https://download.eclipse.org/jgit/maven"

addSbtPlugin("com.github.sbt" % "sbt-ghpages" % "0.8.0")
