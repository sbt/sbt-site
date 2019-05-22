package com.typesafe.sbt.site

import java.nio.file.Files

import sbt.Defaults.ConfigGlobal
import sbt.Keys.{excludeFilter, includeFilter, sourceDirectory, watchSources}
import sbt.internal.io.Source
import sbt.util.CacheStoreFactory
import sbt.util.FileInfo.Style
import sbt.{ChangeReport, Configuration, File, FileFilter, Setting, State}

object Compat {

  type Process = scala.sys.process.Process
  val Process = scala.sys.process.Process
  type ProcessLogger = scala.sys.process.ProcessLogger

  def cached(cacheBaseDirectory: File, inStyle: Style, outStyle: Style)(action: (ChangeReport[File], ChangeReport[File]) => Set[File]): Set[File] => Set[File] =
    sbt.util.FileFunction.cached(CacheStoreFactory(cacheBaseDirectory), inStyle = inStyle, outStyle = outStyle)(action = action)

  val genSources: State => Seq[File] = {
    import scala.collection.JavaConverters._

    def sourceField[A](name: String): Source => A = {
      val f = classOf[Source].getDeclaredField(name)
      f.setAccessible(true)
      src => f.get(src).asInstanceOf[A]
    }

    val baseField = sourceField[File]("base")
    val includeField = sourceField[FileFilter]("includeFilter")
    val excludeField = sourceField[FileFilter]("excludeFilter")

    (s: State) => Preview.runTask(watchSources, s).flatMap { src =>
      val base = baseField(src)
      val include = includeField(src)
      val exclude = excludeField(src)

      try {
        Files.find(
          base.toPath,
          64,
          (f, _) => include.accept(f.toFile) && !exclude.accept(f.toFile)
        ).iterator().asScala.map(_.toFile).toList
      } catch {
        case _: java.nio.file.NoSuchFileException =>
          Nil
      }
    }
  }

  def watchSettings(config: Configuration): Seq[Setting[_]] =
    Seq(
      ConfigGlobal / watchSources += new Source(
        base = (sourceDirectory in config).value,
        includeFilter = (includeFilter in config).value,
        excludeFilter = (excludeFilter in config).value
      )
    )

  val CacheStore = sbt.util.CacheStore

}
