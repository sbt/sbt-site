package com.typesafe.sbt
package site

import sbt._
import Keys._
import SbtSite.SiteKeys.siteMappings
import scala.util.Try
import com.typesafe.config.ConfigFactory

object GitbookSupport {
  val Gitbook = config("gitbook")

  val gitbookInstallDir = TaskKey[Option[File]]("gitbook-install-dir", "Directory in which to install Gitbook; useful in CI environment")

  def settings(config: Configuration = Gitbook): Seq[Setting[_]] =
    Generator.directorySettings(config) ++
    Generator.watchSettings(config) ++ // TODO - this may need to be optional.
    inConfig(config)(Seq(
      includeFilter := AllPassFilter,
      gitbookInstallDir := None,
      mappings := GitbookRunner.build(
        sourceDirectory.value,
        target.value,
        includeFilter.value,
        gitbookInstallDir.value,
        streams.value
      )
    ))
}

/** Helper class with implementations of tasks. */
object GitbookRunner {
  def build(src: File, target: File, inc: FileFilter, installDir: Option[File], s: TaskStreams): Seq[(File, String)] = {
    val runEnv = installDir.map("HOME" -> _.getAbsolutePath).toSeq
    def run(cmd: String*) =
      Process(cmd.toSeq, Some(src), runEnv: _*) ! s.log match {
        case 0 => ()
        case n => sys.error(s"Could not run `${cmd.mkString(" ")}`, error: $n")
      }

    def install(installDir: File): String = {
      if (!installDir.exists) {
        IO.write(installDir / ".gitignore", "*")
        IO.write(installDir / ".gitconfig",
          """|# Enables installation from networks where git:// access is blocked
             |[url "https://github.com/"]
             |  insteadOf = git://github.com/
             |""".stripMargin)
      }

      val installPath = installDir.getAbsolutePath
      val gitbookPath = s"$installPath/bin/gitbook"

      run("npm", "install", "-g", "--prefix", installPath, "gitbook-cli")
      run(gitbookPath, "install")
      gitbookPath
    }

    val gitbook = installDir.map(install).getOrElse("gitbook")

    run(gitbook, "build")

    val output = outputDir(src)
    if (output.getCanonicalPath != target.getCanonicalPath) {
      s.log.warn(s"""|Output directory ${output} does not match the target ${target}.
                     |We are going to copy the files over, but you might want to change
                     |${bookJson(src)} so clean task cleans.""".stripMargin)
      IO.copyDirectory(output, target, overwrite = true, preserveLastModified = true)
    }

    // Figure out what was generated.
    for {
      (file, name) <- (target ** inc --- target pair relativeTo(target))
    } yield file -> name
  }

  def bookJson(src: File): File = src / "book.json"
  val defaultOutputDir = "_book"

  def outputDir(src: File): File = {
    val bookConfig = ConfigFactory.parseFile(bookJson(src))
    val output = Try(bookConfig.getString("output")).getOrElse(defaultOutputDir)

    Path.resolve(src)(file(output)) getOrElse {
      sys.error("Unable to resolve $output directory.")
    }
  }

}
