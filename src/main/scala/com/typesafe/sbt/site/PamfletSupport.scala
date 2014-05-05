package com.typesafe.sbt
package site

import sbt._
import Keys._

object PamfletSupport {
  val Pamflet = config("pamflet")

  def settings(config: Configuration = Pamflet): Seq[Setting[_]] =
    Generator.directorySettings(config) ++
    Seq(
      // Note: txt is used for search index.
      includeFilter in config := AllPassFilter
    ) ++ inConfig(config)(Seq(
      mappings <<= (sourceDirectory, target, includeFilter) map PamfletRunner.run
    )) ++
    Generator.watchSettings(config) // TODO - this may need to be optional.
}

import pamflet._

object PamfletRunner {
  def run(input: File, output: File, includeFilter: FileFilter): Seq[(File, String)] = {
    val storage = FileStorage(input)
    Produce(storage.globalized, output)
    output ** includeFilter --- output x relativeTo(output)
  }
}