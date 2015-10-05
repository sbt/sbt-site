package com.typesafe.sbt
package site

import sbt._
import Keys._

/** Pamflet generator. */
object PamfletSupport extends AutoPlugin {
  import pamflet._
  override def requires = SbtSite
  override def trigger = noTrigger
  object autoImport {
    val Pamflet = config("pamflet")
  }

  import autoImport._

  override def projectSettings: Seq[Setting[_]] =
    SiteHelpers.directorySettings(Pamflet) ++
      Seq(
        // Note: txt is used for search index.
        includeFilter in Pamflet := AllPassFilter
      ) ++ inConfig(Pamflet)(
      Seq(
        mappings <<= (sourceDirectory, target, includeFilter) map run,
        SiteHelpers.addMappingsToSiteDir(mappings, "TODO")
      )) ++
      SiteHelpers.watchSettings(Pamflet)

  private[sbt] def run(
    input: File,
    output: File,
    includeFilter: FileFilter): Seq[(File, String)] = {
    val storage = FileStorage(input)
    Produce(storage.globalized, output)
    output ** includeFilter --- output pair relativeTo(output)
  }
}
