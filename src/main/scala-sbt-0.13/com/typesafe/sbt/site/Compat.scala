package com.typesafe.sbt.site

import sbt._
import sbt.Keys._
import sbt.FilesInfo.Style

object Compat {

  type Process = sbt.Process
  val Process = sbt.Process
  type ProcessLogger = sbt.ProcessLogger

  def cached(cacheBaseDirectory: File, inStyle: Style, outStyle: Style)(action: (ChangeReport[File], ChangeReport[File]) => Set[File]): Set[File] => Set[File] = 
    FileFunction.cached(cacheBaseDirectory = cacheBaseDirectory)(inStyle = inStyle, outStyle = outStyle)(action = action)

  val genSources = (s: State) => {
    Preview.runTask(watchSources, s)
  }

  def watchSettings(config: Configuration): Seq[Setting[_]] =
    Seq(
      watchSources in Global ++= (sourceDirectory in config).value.***.get
    )

  def CacheStore(file: File) = file

  implicit class FileOps(file: File) {
    def allPaths = file.***
  }

}
