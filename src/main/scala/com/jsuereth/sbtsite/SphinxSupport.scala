package com.jsuereth.sbtsite

import sbt._
import Keys._

import SiteKeys.siteMappings

object SphinxSupport {
  val Sphinx = config("sphinx")
  
  val settings: Seq[Setting[_]] =
    Seq(
      sourceDirectory in Sphinx <<= sourceDirectory(_ / "sphinx"),
      target in Sphinx <<= target(_ / "sphinx"),
      // Note: txt is used for search index.
      includeFilter in Sphinx := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "*.txt")
    ) ++ inConfig(Sphinx)(Seq(
      mappings <<= (sourceDirectory, target, includeFilter, version, streams) map SphinxImpl.generate
    ))
}

object SphinxImpl {
  def generate(src: File, target: File, inc: FileFilter, s: TaskStreams): Seq[(File, String)] =
    generate(src, target, inc, Nil, s)
  def generate(src: File, target: File, inc: FileFilter, version: String, s: TaskStreams): Seq[(File, String)] = {
    val binV = CrossVersion.binaryVersion(version, "")
    val extraArgs = ("-Dversion=" + binV) :: ("-Drelease=" + version) :: Nil
    generate(src, target, inc, extraArgs, s)
  }
  def generate(src: File, target: File, inc: FileFilter, extraArgs: List[String], s: TaskStreams): Seq[(File, String)] = {
     val sphinxCommand = Seq("sphinx-build", "-b", "html") ++ extraArgs ++ Seq(src.getAbsolutePath, target.getAbsolutePath)
     sbt.Process(sphinxCommand, Some(src)) ! s.log match {
       case 0 => ()
       case n => sys.error("Failed to run sphinx-build.  Exit code: " + n)
     }
     for { 
       (file, name) <- (target ** inc x relativeTo(target))
     } yield file -> name
  }
}