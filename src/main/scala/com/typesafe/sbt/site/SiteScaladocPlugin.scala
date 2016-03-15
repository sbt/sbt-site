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

  override val projectSettings: Seq[Setting[_]] =
    Seq(
      siteSubdirName in SiteScaladoc := "latest/api"
    ) ++
      SiteHelpers.addMappingsToSiteDir(mappings in packageDoc in Compile, siteSubdirName in SiteScaladoc)
}
