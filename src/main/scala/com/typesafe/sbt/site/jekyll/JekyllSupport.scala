package com.typesafe.sbt.site.jekyll

import com.typesafe.sbt.site.{SbtSite, SiteHelpers}
import sbt.Keys._
import sbt._
object JekyllSupport extends AutoPlugin {
  override def requires = SbtSite
  override def trigger = noTrigger

  object autoImport {
    val Jekyll = config("jekyll")
    val requiredGems = SettingKey[Map[String, String]](
      "jekyll-required-gems", "Required gem + versions for this build.")
    val checkGems = TaskKey[Unit](
      "jekyll-check-gems", "Tests whether or not all required gems are available.")
  }
  import autoImport._
  override def projectSettings: Seq[Setting[_]] =
    SiteHelpers.directorySettings(Jekyll) ++
      Seq(
        includeFilter in Jekyll := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "CNAME" | ".nojekyll"),
        requiredGems := Map.empty
      ) ++ inConfig(Jekyll)(
      Seq(
        checkGems := SiteHelpers.checkGems(requiredGems.value, streams.value),
        mappings := {
          val cg = checkGems.value
          generate(sourceDirectory.value, target.value, includeFilter.value, streams.value)
        },
        SiteHelpers.addMappingsToSiteDir(mappings, "TODO")
      )) ++ SiteHelpers.watchSettings(Jekyll)


  // TODO - Add command line args and the like.
  final def generate(
    src: File,
    target: File,
    inc: FileFilter,
    s: TaskStreams): Seq[(File, String)] = {
    // Run Jekyll
    sbt.Process(Seq("jekyll", "build", "-d", target.getAbsolutePath), Some(src)) ! s.log match {
      case 0 => ()
      case n => sys.error("Could not run jekyll, error: " + n)
    }
    // Figure out what was generated.
    for {
      (file, name) <- target ** inc --- target pair relativeTo(target)
    } yield file -> name
  }
}
