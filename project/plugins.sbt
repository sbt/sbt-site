addSbtPlugin("com.github.sbt" % "sbt-site-paradox" % "1.5.0")
//#sbt-ghpages
addSbtPlugin("com.github.sbt" % "sbt-ghpages" % "0.8.0")
//#sbt-ghpages
addSbtPlugin("com.github.sbt" % "sbt-paradox-material-theme" % "0.7.0")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.10.6") // https://www.scala-sbt.org/sbt-paradox-material-theme/getting-started.html#jdk-11-
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.12")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
