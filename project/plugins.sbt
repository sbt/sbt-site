addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.2.0-RC1")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.2.7")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
