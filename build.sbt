
sbtPlugin := true

name := "sbt-site"

organization := "com.typesafe.sbt"

licenses += ("BSD 3-Clause", url("http://opensource.org/licenses/BSD-3-Clause"))

scalaVersion := "2.10.6"

scalaVersion in Global := "2.10.6"

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-jetty" % "0.6.8",
  "net.databinder" %% "pamflet-library"  % "0.6.0",
  "org.yaml"        % "snakeyaml"        % "1.13",
  "com.typesafe"    % "config"           % "1.2.1", // Last version to support Java 1.6
  "org.asciidoctor" % "asciidoctorj"     % "1.5.2"
)

scriptedSettings

scriptedLaunchOpts += "-Dproject.version="+version.value

// scriptedBufferLog := false
