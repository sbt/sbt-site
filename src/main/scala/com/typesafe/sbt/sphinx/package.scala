package com.typesafe.sbt

import sbt._
import Keys._

package object sphinx {
  def generate(runner: SphinxRunner, src: File, target: File, inc: FileFilter, s: TaskStreams): Seq[(File, String)] = {
     runner.generateHtml(src, target, s.log)
     for {
       (file, name) <- (target ** inc x relativeTo(target))
     } yield file -> name
  }
  def newRunner(options: Map[String,String]): SphinxRunner = new CommandLineSphinxRunner(options)
}
