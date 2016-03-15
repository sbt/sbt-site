package com.typesafe.sbt.site.pamflet

import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._
import pamflet._

/** Pamflet generator. */
object PamfletPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger
  object autoImport {
    val Pamflet = config("pamflet")
  }

  import autoImport._
  override def projectSettings = pamfletSettings(Pamflet)

  def pamfletSettings(config: Configuration): Seq[Setting[_]] =
      inConfig(config)(
        Seq(
          includeFilter := AllPassFilter,
          mappings := generate(sourceDirectory.value, target.value, includeFilter.value),
          siteSubdirName := ""
        )
      ) ++
        SiteHelpers.directorySettings(config) ++
        SiteHelpers.watchSettings(config) ++
        SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)

  /** Run pamflet in sbt's JVM. */
  private[sbt] def generate(
    input: File,
    output: File,
    includeFilter: FileFilter): Seq[(File, String)] = {
    val storage = FileStorage(input)
    Produce(storage.globalized, output)
    output ** includeFilter --- output pair relativeTo(output)
  }
}
