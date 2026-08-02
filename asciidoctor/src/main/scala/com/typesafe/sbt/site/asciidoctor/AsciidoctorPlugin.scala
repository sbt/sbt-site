package com.typesafe.sbt.site.asciidoctor

import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.util.SiteHelpers
import org.asciidoctor.Asciidoctor.Factory
import org.asciidoctor.Attributes
import org.asciidoctor.Options
import org.asciidoctor.SafeMode
import org.asciidoctor.jruby.AsciiDocDirectoryWalker
import sbt.Keys._
import sbt._

/** Asciidoctor generator. */
object AsciidoctorPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger

  object autoImport extends AsciidoctorKeys {
    val Asciidoctor = config("asciidoctor")
  }
  import autoImport._
  override lazy val globalSettings = Seq(
    asciidoctorAttributes := Map()
  )
  override def projectSettings = asciidoctorSettings(Asciidoctor)

  /** Creates settings necessary for running Asciidoctor in the given configuration. */
  def asciidoctorSettings(config: Configuration): Seq[Setting[?]] =
    inConfig(config)(
      Seq(
        includeFilter := AllPassFilter,
        mappings := generate(
          sourceDirectory.value,
          target.value,
          includeFilter.value,
          version.value,
          asciidoctorAttributes.value),
        siteSubdirName := ""
      )
    ) ++
      SiteHelpers.directorySettings(config) ++
      SiteHelpers.watchSettings(config) ++
      SiteHelpers.addMappingsToSiteDir(config / mappings, config / siteSubdirName)

  /** Run asciidoctor in new ClassLoader. */
  private[sbt] def generate(
      input: File,
      output: File,
      includeFilter: FileFilter,
      version: String,
      userSetAsciidoctorAttributes: Map[String, String]): Seq[(File, String)] = {
    val oldContextClassLoader = Thread.currentThread().getContextClassLoader
    Thread.currentThread().setContextClassLoader(this.getClass.getClassLoader)
    val asciidoctor = Factory.create()
    asciidoctor.requireLibrary("asciidoctor-diagram")
    if (!output.exists) output.mkdirs()

    //pass project.version to asciidoctor as attribute project-version
    //need to do this explicitly through HashMap because otherwise JRuby complains
    val attributes = Attributes
      .builder()
      .attribute("project-version", version)

    // Add user configured attributes into the mix
    userSetAsciidoctorAttributes.foreach { case (key, value) =>
      attributes.attribute(key, value)
    }

    val options = Options
      .builder()
      .toDir(output)
      .safe(SafeMode.UNSAFE)
      .attributes(attributes.build())
      .build()

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
