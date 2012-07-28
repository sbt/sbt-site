package com.jsuereth.sbtsite

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
    val props = input / "template.properties"
    if(!output.exists) output.mkdirs()
    //if(!props.exists) sys.error("CANNOT FIND " + props)
    val storage = FileStorage(input, 
                   new File(input, "template.properties") match {
                      case file if file.exists => Some(file)
                      case _ => None
                   })
    Produce(storage.contents, output)
    output ** includeFilter --- output x relativeTo(output)
  }
}