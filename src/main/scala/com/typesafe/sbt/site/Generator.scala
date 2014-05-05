package com.typesafe.sbt
package site

import sbt._
import Keys._

object Generator {
  def directorySettings(config: Configuration): Seq[Setting[_]] =
  	inConfig(config)(Seq(
  		sourceDirectory := sourceDirectory.value / config.name,
      target := target.value / config.name
  	))
  def watchSettings(config: Configuration): Seq[Setting[_]] =
  	Seq(
  		watchSources in Global <++= (sourceDirectory in config) map { _.***.get }
  	)
  def checkGems(requirements: Map[String,String], s: TaskStreams): Unit = {
    /** Generates an error message if the gem's version isn't appropriate for this build. */
    def makeError(gem: String, version: String, current: Option[String]): Option[String] = current match {
      case Some(v) if v contains version  => None
      case Some(v) => Some("This build requires the gem [%s (%s)] but found version (%s) instead." format(gem, version, v))
      case None    => Some("This build requires the gem [%s (%s)] to be installed." format (gem, version))
    }
    val errors = for {
      (gem, version) <- requirements
      error <- makeError(gem, version, getGemVersion(gem))
    } yield error
    if(!errors.isEmpty)
      sys.error(errors.mkString("Gem version requirements failed:\n\t", "\n\t", "\n"))
  }

  /** Checks versions of gems installed. */
  private def getGemVersion(gem: String): Option[String] = {
    val installed = Seq("gem", "list", "--local", gem).!!
    """\((.+)\)""".r.findFirstMatchIn(installed) match {
      case None    => None
      case Some(m) => Some(m.group(1))
    }
  }
}
