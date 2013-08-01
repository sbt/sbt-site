package com.typesafe.sbt
package site

import sbt._
import Keys._

@deprecated("0.7.0","Pamflet support was disabled in version 0.7.0 due to lack of Scala 2.10 binaries for pamflet")
object PamfletSupport {
  val Pamflet = config("pamflet")

  val settings: Seq[Setting[_]] =
    Seq(
      sourceDirectory in Pamflet <<= sourceDirectory(_ / "pamflet"),
      target in Pamflet <<= target(_ / "pamflet"),
      // Note: txt is used for search index.
      includeFilter in Pamflet := AllPassFilter
    ) ++ inConfig(Pamflet)(Seq(
      mappings <<= (sourceDirectory, target, includeFilter) map PamfletRunner.run
    ))
}


object PamfletRunner {
  def run(input: File, output: File, includeFilter: FileFilter): Seq[(File, String)] = 
    sys.error("Pamflet support was disabled in version 0.7.0 due to lack of Scala 2.10 binaries")
}