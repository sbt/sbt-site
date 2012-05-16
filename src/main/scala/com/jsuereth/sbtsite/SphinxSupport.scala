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
      includeFilter in Sphinx := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif")
    ) ++ inConfig(Sphinx)(Seq(
      mappings <<= (sourceDirectory, target, includeFilter, streams) map SphinxImpl.generate
    ))
}

object SphinxImpl {
  final def generate(src: File, target: File, inc: FileFilter, s: TaskStreams): Seq[(File, String)] = {
     sbt.Process(Seq("sphinx-build", "-b", "html", src.getAbsolutePath, target.getAbsolutePath), Some(src)) ! s.log match {
       case 0 => ()
       case n => sys.error("Failed to run sphinx-build.  Exit code: " + n)
     }
     for { 
       (file, name) <- (target ** inc x relativeTo(target))
     } yield file -> name
  }
}