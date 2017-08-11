addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.2.1")
//#sbt-ghpages
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.1")
//#sbt-ghpages

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
