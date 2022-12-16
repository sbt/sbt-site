addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")
//#sbt-ghpages
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
//#sbt-ghpages
addSbtPlugin("io.github.jonas" % "sbt-paradox-material-theme" % "0.6.0")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
