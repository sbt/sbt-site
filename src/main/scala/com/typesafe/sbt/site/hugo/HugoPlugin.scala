package com.typesafe.sbt.site.hugo

import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePreviewPlugin.autoImport.previewFixedPort
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import sbt._, Keys._
import java.net.URI

object HugoPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger

  object autoImport {
    val Hugo = config("hugo")
    val minimumHugoVersion = settingKey[String]("minimum-hugo-version")
    val baseURL = settingKey[URI]("base-url")
    val checkHugoVersion = taskKey[Unit]("check-hugo-version")
  }

  import autoImport._

  override def projectSettings = hugoSettings(Hugo)

  /** Creates the settings necessary for running hugo in the given configuration. */
  def hugoSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        includeFilter := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "CNAME"),
        minimumHugoVersion := "0.15",
        baseURL := new URI(s"http://127.0.0.1:${previewFixedPort.value.getOrElse(1313)}"),
        checkHugoVersion := unsafeCheckHugoVersion(minimumHugoVersion.value, streams.value).get,
        mappings := {
          val _ = checkHugoVersion.value // sadly relying on effects here, as is the idiom in sbt-site
          generate(sourceDirectory.value, target.value, includeFilter.value, baseURL.value, streams.value)
        },
        siteSubdirName := ""
      )
    ) ++
      SiteHelpers.directorySettings(config) ++
      SiteHelpers.watchSettings(config) ++
      SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)

  /** Run hugo via fork. */
  private[sbt] def generate(
    src: File,
    target: File,
    inc: FileFilter,
    baseURL: URI,
    s: TaskStreams): Seq[(File, String)] = {
    sbt.Process(Seq("hugo", "-d", target.getAbsolutePath, "--baseURL", baseURL.toString), Some(src)) ! s.log match {
      case 0 => ()
      case n => sys.error("Could not run hugo binary, error: " + n)
    }
    for {
      (file, name) <- target ** inc --- target pair relativeTo(target)
    } yield file -> name
  }

  import scala.util.{Try, Success, Failure}

  def unsafeCheckHugoVersion(minimumVersion: String, s: TaskStreams): Try[Unit] = {
    s.log.debug("checking for the installed version of hugo...")
    for {
      installed <- Try(Seq("hugo", "-", "version").!!)
      extracted <- Try("""v(\d\.\d+)\D""".r.findFirstMatchIn(installed.trim))
      _ <- extracted.fold(Failure(new RuntimeException("Hugo is not currently installed!")): Try[Unit]
                    )(v => if(v.group(1) >= minimumVersion) Success(())
                           else Failure(new RuntimeException("The current version of Hugo installed is not new enough to build this project.")))
    } yield ()
  }

}