package com.typesafe.sbt.site.asciidoctor

import java.util
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import org.asciidoctor.Asciidoctor.Factory
import org.asciidoctor.{AsciiDocDirectoryWalker, Options, SafeMode}
import sbt.Keys._
import sbt._

/** Asciidoctor generator. */
object AsciidoctorPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger

  object autoImport {
    val Asciidoctor = config("asciidoctor")
  }
  import autoImport._

  override def projectSettings = asciidoctorSettings(Asciidoctor)

  /** Creates settings necessary for running Asciidoctor in the given configuration. */
  def asciidoctorSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        includeFilter := AllPassFilter,
        mappings := generate(sourceDirectory.value, target.value, includeFilter.value, version.value),
        siteSubdirName := ""
      )
    ) ++
      SiteHelpers.directorySettings(config) ++
      SiteHelpers.watchSettings(config) ++
      SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)

  /** Run asciidoctor in new ClassLoader. */
  private[sbt] def generate(
    input: File,
    output: File,
    includeFilter: FileFilter,
    version: String): Seq[(File, String)] = {
    val oldContextClassLoader = Thread.currentThread().getContextClassLoader
    Thread.currentThread().setContextClassLoader(this.getClass.getClassLoader)
    val asciidoctor = Factory.create()
    asciidoctor.requireLibrary("asciidoctor-diagram")
    if (!output.exists) output.mkdirs()
    val options = new Options
    options.setToDir(output.getAbsolutePath)
    options.setDestinationDir(output.getAbsolutePath)
    options.setSafe(SafeMode.UNSAFE)
    //pass project.version to asciidoctor as attribute project-version
    //need to do this explicitly through HashMap because otherwise JRuby complains
    val attributes = new util.HashMap[String, AnyRef]()
    attributes.put("project-version", version)
    options.setAttributes(attributes)
    asciidoctor.convertDirectory(new AsciiDocDirectoryWalker(input.getAbsolutePath), options)
    val inputImages = input / "images"
    if (inputImages.exists()) {
      val outputImages = output / "images"
      IO.copyDirectory(inputImages, outputImages, overwrite = true)
    }
    Thread.currentThread().setContextClassLoader(oldContextClassLoader)
    output ** includeFilter --- output pair Path.relativeTo(output)
  }
}
