package com.typesafe.sbt.site

import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._

/** Simple plugin for adding Scaladoc to the site content. */
object SiteScaladocPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger
  object autoImport {
    val SiteScaladoc = config("siteScaladoc")
  }
  import autoImport._

  override val projectSettings = scaladocSettings(SiteScaladoc)

  def scaladocSettings(
    config: Configuration,
    scaladocMappings: TaskKey[Seq[(File, String)]] = (mappings in (Compile, packageDoc)),
    scaladocDir: String = "latest/api"
  ): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        siteSubdirName := scaladocDir,
        mappings := scaladocMappings.value
      )
    ) ++
      SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)
}
