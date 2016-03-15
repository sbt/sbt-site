package com.typesafe.sbt.site.util

import sbt.Keys._
import sbt._

/**
 * Support utilities for ruby-based generators.
 * @since 10/29/15
 */
object RubyHelpers {
  /**
   * Keys for ruby-based generators.
   * @since 10/29/15
   */
  trait RubyKeys {
    val requiredGems = SettingKey[Map[String, String]](
      "required-gems", "Required gem + versions for this build.")
    val checkGems = TaskKey[Unit](
      "check-gems", "Tests whether or not all required gems are available.")
  }

  /** Checks to make sure proper versions of Jekyll Ruby Gems are installed. */
  def checkGems(requirements: Map[String, String], s: TaskStreams): Unit = {
    // Generates an error message if the gem's version isn't appropriate for this build.
    def makeError(
      gem: String,
      version: String,
      current: Option[String]): Option[String] = current match {
      case Some(v) if v contains version => None
      case Some(v) => Some(
        "This build requires the gem [%s (%s)] but found version (%s) instead." format(gem, version, v))
      case None => Some(
        "This build requires the gem [%s (%s)] to be installed." format(gem, version))
    }
    val errors = for {
      (gem, version) <- requirements
      error <- makeError(gem, version, getGemVersion(gem))
    } yield error
    if (errors.nonEmpty) {
      sys.error(errors.mkString("Gem version requirements failed:\n\t", "\n\t", "\n"))
    }
  }

  /** Checks versions of gems installed. */
  def getGemVersion(gem: String): Option[String] = {
    val installed = Seq("gem", "list", "--local", gem).!!
    """\((.+)\)""".r.findFirstMatchIn(installed) match {
      case None => None
      case Some(m) => Some(m.group(1))
    }
  }
}
