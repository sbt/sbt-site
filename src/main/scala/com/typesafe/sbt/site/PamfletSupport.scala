package com.typesafe.sbt
package site

import sbt._
import Keys._

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

import pamflet._

object PamfletRunner {
  def run(input: File, output: File, includeFilter: FileFilter): Seq[(File, String)] = {
    val storage = FileStorage(input)
    Produce(storage.globalized, output)
    output ** includeFilter --- output x relativeTo(output)
  }
}