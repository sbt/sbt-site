package com.jsuereth.sbtsite
package sphinx

import sbt._
import Keys._

import SiteKeys.siteMappings


/** A generic interface to sphinx that will remain binary compatible in this plugin. */
trait SphinxRunner {
   def generateHtml(src: File, target: File, log: Logger): Unit
}


/** A hidden implementation detail that lets us run command-line sphinx.
 * Exposes the SphinxRunner interface.
 */
private[sphinx] class CommandLineSphinxRunner(configOverrides: Map[String,String]) extends SphinxRunner {
  private val configOverrideParams: Seq[String] =
    (for((name, value) <- configOverrides)
     yield "-D" + name + "=" + value).toSeq
  def generateHtml(src: File, target: File, log: Logger): Unit = {
    val cmd = {
       Seq("sphinx-build", "-b", "html") ++
       configOverrideParams ++
       Seq("sphinx-build", "-b", "html")
    }
    sbt.Process(cmd, Some(src)) ! log match {
       case 0 => ()
       case n => sys.error("Failed to run sphinx-build.  Exit code: " + n)
     }
  }
}
