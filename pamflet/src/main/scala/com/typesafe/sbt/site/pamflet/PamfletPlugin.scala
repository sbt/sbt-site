package com.typesafe.sbt.site.pamflet

import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._
import sbtcompat.PluginCompat
import pamflet._
import xsbti.FileConverter

/** Pamflet generator. */
object PamfletPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger
  object autoImport {
    val Pamflet = config("pamflet")
    val pamfletFencePlugins = SettingKey[Seq[FencePlugin]]("pamfletFencePlugins")
  }

  import autoImport._
  override def projectSettings = pamfletSettings(Pamflet)

  def pamfletSettings(config: Configuration): Seq[Setting[?]] =
      inConfig(config)(
        Seq(
          includeFilter := AllPassFilter,
          pamfletFencePlugins := (config / pamfletFencePlugins).?.value.getOrElse(Nil),
          mappings := {
            implicit val converter: FileConverter = fileConverter.value
            PluginCompat.toFileRefsMapping(generate(sourceDirectory.value, target.value, includeFilter.value, pamfletFencePlugins.value))
          },
          siteSubdirName := ""
        )
      ) ++
        SiteHelpers.directorySettings(config) ++
        SiteHelpers.watchSettings(config) ++
        SiteHelpers.addMappingsToSiteDir(config / mappings, config / siteSubdirName)

  /** Run pamflet in sbt's JVM. */
  private[sbt] def generate(
    input: File,
    output: File,
    includeFilter: FileFilter,
    fencePlugins: Seq[FencePlugin]): Seq[(File, String)] = {
    val storage = FileStorage(input, fencePlugins.toList)
    Produce(storage.globalized, output)
    output ** includeFilter --- output pair Path.relativeTo(output)
  }
}
