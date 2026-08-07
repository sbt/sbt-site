addSbtPlugin("com.github.sbt" % "sbt-site-paradox" % "1.7.0")
//#sbt-ghpages
addSbtPlugin("com.github.sbt" % "sbt-ghpages" % "0.9.0")
//#sbt-ghpages
addSbtPlugin("com.github.sbt" % "sbt-paradox-material-theme" % "0.7.0")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.11.0") // https://www.scala-sbt.org/sbt-paradox-material-theme/getting-started.html#jdk-11-
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.12.0")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
