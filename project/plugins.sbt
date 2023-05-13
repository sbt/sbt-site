addSbtPlugin("com.github.sbt" % "sbt-site-paradox" % "1.5.0")
//#sbt-ghpages
addSbtPlugin(
  ("com.github.sbt" % "sbt-ghpages" % "0.7.0")
    // sbt-ghpages depends on sbt-site 1.4.1, which pulls Scala XML 1.x
    .exclude("org.scala-lang.modules", "scala-xml_2.12")
)
//#sbt-ghpages
addSbtPlugin("io.github.jonas" % "sbt-paradox-material-theme" % "0.6.0")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % "always"
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-parser-combinators" % "always"
