package com.typesafe.sbt
package site

import sbt._
import Keys._
import SbtSite.SiteKeys.siteMappings
import collection.immutable
import java.io.FileReader

object NanocSupport {
  val Nanoc = config("nanoc")

  val requiredGems = SettingKey[Map[String,String]]("nanoc-required-gems", "Required gem + versions for this build.")
  val checkGems = TaskKey[Unit]("nanoc-check-gems", "Tests whether or not all required gems are available.")
  def settings(config: Configuration = Nanoc): Seq[Setting[_]] =
    Generator.directorySettings(config) ++
    Seq(
      includeFilter in config := AllPassFilter,
      requiredGems := Map.empty
    ) ++ inConfig(config)(Seq(
      checkGems := Generator.checkGems(requiredGems.value, streams.value),
      mappings := {
        val cg = checkGems.value
        NanocImpl.generate(sourceDirectory.value, target.value, includeFilter.value, streams.value)
      }
    )) ++
    Generator.watchSettings(config) // TODO - this may need to be optional.
}

/** Helper class with implementations of tasks. */
object NanocImpl {
  // TODO - Add command line args and the like.
  final def generate(src: File, target: File, inc: FileFilter, s: TaskStreams): Seq[(File, String)] = {
    // Run nanoc
    sbt.Process(Seq("nanoc"), Some(src)) ! s.log match {
      case 0 => ()
      case n => sys.error("Could not run nanoc, error: " + n)
    }
    val output = outputDir(src)
    if (output.getCanonicalPath != target.getCanonicalPath) {
      s.log.warn(s"""Output directory ${output.toString} does not match the target ${target.toString}.
We are going to copy the files over, but you might want to change
${yamlFileName(src)} so clean task cleans.""")
      IO.copyDirectory(output, target, overwrite = true, preserveLastModified = true)
    }

    // Figure out what was generated.
    for {
      (file, name) <- (target ** inc --- target pair relativeTo(target))
    } yield file -> name
  }

  def yamlFileName(src: File): File = src / "nanoc.yaml"

  def outputDir(src: File): File = {
    val yaml = nanocYaml(yamlFileName(src))
    // it's output_dir in nanoc 3.x, and according to http://nanoc.ws/docs/nanoc-4-upgrade-guide/ it's
    // going to be changed to build_dir
    val output: String = ((yaml get "output_dir") orElse (yaml get "build_dir")) map {_.toString} getOrElse {
      sys.error("Neither output_dir nor build_dir was found in ${yamlFileName(src).toString}!")
    }
    Path.resolve(src)(file(output)) getOrElse {
      sys.error("Unable to resolve $output directory.")
    }
  }

  def nanocYaml(configFile: File): immutable.Map[String, Any] = {
    import org.yaml.snakeyaml.Yaml
    import java.util.{Map => JMap}
    import collection.JavaConversions._
    if (!configFile.exists) {
      sys.error(s"""$configFile is not found!""")
    }
    val yaml = new Yaml()
    val x = yaml.load(new FileReader(configFile)).asInstanceOf[JMap[String, Any]]
    immutable.Map.empty[String, Any] ++ x
  }
}
