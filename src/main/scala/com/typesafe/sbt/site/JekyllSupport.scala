package com.typesafe.sbt
package site

import sbt._
import Keys._
import SbtSite.SiteKeys.siteMappings

object JekyllSupport {
  val Jekyll = config("jekyll")

  val requiredGems = SettingKey[Map[String,String]]("jekyll-required-gems", "Required gem + versions for this build.")
  val checkGems = TaskKey[Unit]("jekyll-check-gems", "Tests whether or not all required gems are available.")
  def settings(config: Configuration = Jekyll): Seq[Setting[_]] =
    Generator.directorySettings(config) ++
    Seq(
      includeFilter in config := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "CNAME" | ".nojekyll"),
      requiredGems := Map.empty
      //(mappings in SiteKeys.siteMappings) <++= (mappings in Jekyll),
    ) ++ inConfig(config)(Seq(
      checkGems := Generator.checkGems(requiredGems.value, streams.value),
      mappings := {
        val cg = checkGems.value
        JekyllImpl.generate(sourceDirectory.value, target.value, includeFilter.value, streams.value)
      }
    )) ++ Seq(
      siteMappings ++= (mappings in config).value
    ) ++
    Generator.watchSettings(config) // TODO - this may need to be optional.
}

/** Helper class with implementations of tasks. */
object JekyllImpl {
  // TODO - Add command line args and the like.
  final def generate(src: File, target: File, inc: FileFilter, s: TaskStreams): Seq[(File, String)] = {
    // Run Jekyll
    sbt.Process(Seq("jekyll", "build", "-d", target.getAbsolutePath), Some(src)) ! s.log match {
      case 0 => ()
      case n => sys.error("Could not run jekyll, error: " + n)
    }
    // Figure out what was generated.
    for {
      (file, name) <- (target ** inc --- target x relativeTo(target))
    } yield file -> name
  }
}
