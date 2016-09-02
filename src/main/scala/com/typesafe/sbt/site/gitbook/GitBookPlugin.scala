package com.typesafe.sbt.site.gitbook

import sbt._
import Keys._
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import scala.util.Try
import com.typesafe.config.ConfigFactory

/** GitBook site generator */
object GitBookPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger

  object autoImport {
    val GitBook = config("gitbook")
    val gitbookInstallDir = TaskKey[Option[File]]("gitbook-install-dir", "Directory in which to install Gitbook; useful in CI environment")
  }
  import autoImport._

  override def projectSettings = gitbookSettings(GitBook)

  /** Creates settings necessary for running GitBook in the given configuration. */
  def gitbookSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        includeFilter := AllPassFilter,
        excludeFilter := HiddenFileFilter,
        gitbookInstallDir := None,
        mappings := generate(
          sourceDirectory.value,
          target.value,
          includeFilter.value,
          excludeFilter.value,
          gitbookInstallDir.value,
          streams.value
        ),
        siteSubdirName := ""
      )
    ) ++
      SiteHelpers.directorySettings(config) ++
      SiteHelpers.watchSettings(config) ++
      SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)

  /** Run gitbook commands. */
  private[sbt] def generate(src: File, target: File, inc: FileFilter, exc: FileFilter, installDir: Option[File], s: TaskStreams): Seq[(File, String)] = {
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
    val files = (target ** inc) --- (target ** exc) --- target
    files pair relativeTo(target)
  }

  private[sbt] def bookJson(src: File): File = src / "book.json"
  private[sbt] val defaultOutputDir = "_book"

  private[sbt] def outputDir(src: File): File = {
    val bookConfig = ConfigFactory.parseFile(bookJson(src))
    val output = Try(bookConfig.getString("output")).getOrElse(defaultOutputDir)

    Path.resolve(src)(file(output)) getOrElse {
      sys.error("Unable to resolve $output directory.")
    }
  }

}
