addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")
//#sbt-ghpages
addSbtPlugin("com.github.sbt" % "sbt-ghpages" % "0.7.0")
//#sbt-ghpages
addSbtPlugin("io.github.jonas" % "sbt-paradox-material-theme" % "0.6.0")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
