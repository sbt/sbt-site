package com.typesafe.sbt.site.jekyll

import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.RubyHelpers.RubyKeys
import com.typesafe.sbt.site.util.{RubyHelpers, SiteHelpers}
import sbt.Keys._
import sbt._

/** Support to generate a Jekyll-based website. */
object JekyllPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger

  object autoImport extends RubyKeys {
    val Jekyll = config("jekyll")
  }
  import autoImport._
  override def projectSettings = jekyllSettings(Jekyll)

  /** Creates the settings necessary for running Jekyll in the given configuration. */
  def jekyllSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        checkGems := RubyHelpers.checkGems(requiredGems.value, streams.value),
        requiredGems := Map.empty,
        includeFilter := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "CNAME" | ".nojekyll"),
        mappings := {
          val _ = checkGems.value
          generate(sourceDirectory.value, target.value, includeFilter.value, streams.value)
        },
        siteSubdirName := ""
      )
    ) ++
      SiteHelpers.directorySettings(config) ++
      SiteHelpers.watchSettings(config) ++
      SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)

  /** Run jekyll via fork. TODO - Add command line args and the like. */
  private[sbt] def generate(
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
