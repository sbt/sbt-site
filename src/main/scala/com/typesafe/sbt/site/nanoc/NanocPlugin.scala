package com.typesafe.sbt.site.nanoc

import java.io.FileReader
import com.typesafe.sbt.site.Compat.Process
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.RubyHelpers.RubyKeys
import com.typesafe.sbt.site.util.{RubyHelpers, SiteHelpers}
import sbt.Keys._
import sbt._

import scala.collection.immutable
/** Support to generate a Nanoc-based website. */
object NanocPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger
  object autoImport extends RubyKeys {
    val Nanoc = config("nanoc")
  }

  import autoImport._
  override def projectSettings = nanocSettings(Nanoc)
  def nanocSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        requiredGems := Map.empty,
        checkGems := RubyHelpers.checkGems(requiredGems.value, streams.value),
        includeFilter := AllPassFilter,
        mappings := {
          val cg = checkGems.value
          generate(
            sourceDirectory.value, target.value, includeFilter.value, streams.value)
        },
        siteSubdirName := ""
      )
    ) ++
      SiteHelpers.directorySettings(config) ++
      SiteHelpers.watchSettings(config) ++
      SiteHelpers.addMappingsToSiteDir(config / mappings, config / siteSubdirName)

  /** Run nanoc via fork. TODO - Add command line args and the like. */
  private[sbt] def generate(
    src: File,
    target: File,
    inc: FileFilter,
    s: TaskStreams): Seq[(File, String)] = {
    // Run nanoc
    Process(Seq("nanoc"), Some(src)) ! s.log match {
      case 0 => ()
      case n => sys.error("Could not run nanoc, error: " + n)
    }
    val output = outputDir(src)
    if (output.getCanonicalPath != target.getCanonicalPath) {
      s.log.warn(
        s"""Output directory ${output.toString} does not match the target ${target.toString}.
We are going to copy the files over, but you might want to change
${yamlFileName(src)} so clean task cleans.""")
      IO.copyDirectory(output, target, overwrite = true, preserveLastModified = true)
    }

    // Figure out what was generated.
    for {
      (file, name) <- target ** inc --- target pair Path.relativeTo(target)
    } yield file -> name
  }

  private[sbt] def yamlFileName(src: File): File = src / "nanoc.yaml"

  private[sbt] def outputDir(src: File): File = {
    val yaml = nanocYaml(yamlFileName(src))
    // it's output_dir in nanoc 3.x, and according to https://nanoc.ws/doc/nanoc-4-upgrade-guide/ it's
    // going to be changed to build_dir
    val output: String = ((yaml get "output_dir") orElse (yaml get "build_dir")) map {_.toString} getOrElse {
      sys.error("Neither output_dir nor build_dir was found in ${yamlFileName(src).toString}!")
    }
    Path.resolve(src)(file(output)) getOrElse {
      sys.error("Unable to resolve $output directory.")
    }
  }

  private[sbt] def nanocYaml(configFile: File): immutable.Map[String, Any] = {
    import java.util.{Map ⇒ JMap}

    import org.yaml.snakeyaml.Yaml

    import collection.JavaConverters._
    if (!configFile.exists) {
      sys.error( s"""$configFile is not found!""")
    }
    val yaml = new Yaml()
    val x = yaml.load(new FileReader(configFile)).asInstanceOf[JMap[String, Any]]
    x.asScala.toMap
  }
}
