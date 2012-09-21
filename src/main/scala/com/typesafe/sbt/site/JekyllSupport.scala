package com.typesafe.sbt
package site

import sbt._
import Keys._
import SbtSite.SiteKeys.siteMappings

object JekyllSupport {
  val Jekyll = config("jekyll")

  val RequiredGems = SettingKey[Map[String,String]]("jekyll-required-gems", "Required gem + versions for this build.")
  val CheckGems = TaskKey[Unit]("jekyll-check-gems", "Tests whether or not all required gems are available.")
  val settings: Seq[Setting[_]] =
    Seq(
      sourceDirectory in Jekyll <<= sourceDirectory(_ / "jekyll"),
      target in Jekyll <<= target(_ / "jekyll"),
      includeFilter in Jekyll := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "CNAME" | ".nojekyll"),
      RequiredGems := Map.empty
      //(mappings in SiteKeys.siteMappings) <++= (mappings in Jekyll),
    ) ++ inConfig(Jekyll)(Seq(
      CheckGems <<= (RequiredGems, streams) map JekyllImpl.checkGems,
       mappings <<= (sourceDirectory, target, includeFilter, CheckGems, streams) map {
        (src, t, inc, _, s) => JekyllImpl.generate(src, t, inc, s)
      }
    )) ++ Seq(
      siteMappings <++= mappings in Jekyll
    )
}

/** Helper class with implementations of tasks. */
object JekyllImpl {
  // TODO - Add command line args and the like.
  final def generate(src: File, target: File, inc: FileFilter, s: TaskStreams): Seq[(File, String)] = {
    // Run Jekyll
    sbt.Process(Seq("jekyll", target.getAbsolutePath), Some(src)) ! s.log match {
      case 0 => ()
      case n => sys.error("Could not run jekyll, error: " + n)
    }
    // Figure out what was generated.
    for {
      (file, name) <- (target ** inc x relativeTo(target))
    } yield file -> name
  }

  final def checkGems(requirements: Map[String,String], s: TaskStreams): Unit = {
    /** Generates an error message if the gem's version isn't appropriate for this build. */
    def makeError(gem: String, version: String, current: Option[String]): Option[String] = current match {
      case Some(v) if v contains version  => None
      case Some(v) => Some("This build requires the gem [%s (%s)] but found version (%s) instead." format(gem, version, v))
      case None    => Some("This build requires the gem [%s (%s)] to be installed." format (gem, version))
    }
    val errors = for {
      (gem, version) <- requirements
      error <- makeError(gem, version, getVersion(gem))
    } yield error
    if(!errors.isEmpty)
      sys.error(errors.mkString("Gem version requirements failed:\n\t", "\n\t", "\n"))
  }

  /** Checks versions of gems installed. */
  private def getVersion(gem: String): Option[String] = {
    val installed = Seq("gem", "list", "--local", gem).!!
    """\((.+)\)""".r.findFirstMatchIn(installed) match {
      case None    => None
      case Some(m) => Some(m.group(1))
    }
  }
}
