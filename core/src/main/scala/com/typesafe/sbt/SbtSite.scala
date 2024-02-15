package com.typesafe.sbt

import com.typesafe.sbt.site.SitePlugin
import sbt.Keys

/** Shim for backwards compatibility in `sbt-ghpages` */
object SbtSite {
  object SiteKeys {
    @deprecated("Please upgrade to AutoPlugin and use `makeSite / mappings`", "1.0")
    val siteMappings = SitePlugin.autoImport.makeSite / Keys.mappings
  }
}
