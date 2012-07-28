package com.jsuereth.sbtsite

import sbt._
import Keys._


object SitePlugin extends Plugin {
  object site {
    // TODO - Site key references here  
    import SiteKeys._
    val settings = Seq(
      siteMappings := Seq[(File,String)](),    
      siteDirectory <<= target(_ / "site"),
      siteSourceDirectory <<= sourceDirectory(_ / "site"),
      includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf",
      siteMappings <++= (includeFilter in makeSite, siteSourceDirectory) map { (incs, dir ) =>
        dir ** incs x relativeTo(dir)
      },
      makeSite <<= (siteDirectory, siteMappings) map { (dir, maps) =>
        // TODO - Lazier way to do this.
        for((file, target) <- maps) {
          val tfile = dir / target
          if(file.isDirectory) IO.createDirectory(tfile)
          else IO.copyFile(file,tfile)
        }
        dir      
      }
    )
    /** Convenience functions to add a task of mappings to a site under a nested directory. */
    def addMappingsToSiteDir(mappings: TaskKey[Seq[(File,String)]], nestedDirectory: String): Setting[_] =
      siteMappings <++= mappings map { m =>
        for((f, d) <- m) yield (f, nestedDirectory + "/" + d)
      }
    
    /** Includes scaladoc APIS in site under a "latest/api" directory. */
    def includeScaladoc(alias: String = "latest/api"): Seq[Setting[_]] = 
      Seq(addMappingsToSiteDir(mappings in packageDoc in Compile, alias))
    /** Includes Jekyll generated site under a the root directory. */
    def jekyllSupport(alias: String = ""): Seq[Setting[_]] =
      JekyllSupport.settings ++ Seq(addMappingsToSiteDir(mappings in JekyllSupport.Jekyll, alias))
    /** Includes Sphinx generated site under a the root directory. */
    def sphinxSupport(alias: String = ""): Seq[Setting[_]] = 
      SphinxSupport.settings ++ Seq(addMappingsToSiteDir(mappings in SphinxSupport.Sphinx, alias)) 
    def pamfletSupport(alias: String = ""): Seq[Setting[_]] =
      PamfletSupport.settings ++ Seq(addMappingsToSiteDir(mappings in PamfletSupport.Pamflet, alias))
  }
  // Note: We include helpers so other plugins can 'plug in' to this one without requiring users to use/configure the site plugin.
  override val settings = Seq(
    SiteKeys.siteMappings <<= SiteKeys.siteMappings ?? Seq[(File,String)]()
  )
}