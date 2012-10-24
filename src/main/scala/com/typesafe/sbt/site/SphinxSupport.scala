package com.typesafe.sbt
package site

import sbt._
import sbt.Keys._
import sbt.Project.Initialize
import com.typesafe.sbt.sphinx._

object SphinxSupport {
  val Sphinx = config("sphinx")

  val sphinxPackages = SettingKey[Seq[File]]("sphinx-packages", "Custom Python package sources to install for Sphinx.")
  val sphinxTags = SettingKey[Seq[String]]("sphinx-tags", "Sphinx tags that should be passed when running Sphinx.")
  val sphinxProperties = SettingKey[Map[String, String]]("sphinx-properties", "-D options that should be passed when running Sphinx.")
  val sphinxIncremental = SettingKey[Boolean]("sphinx-incremental", "Use incremental Sphinx building. Off by default.")
  val sphinxInputs = TaskKey[SphinxInputs]("sphinx-inputs", "Combined inputs for the Sphinx runner.")
  val sphinxRunner = TaskKey[SphinxRunner]("sphinx-runner", "The class used to run Sphinx commands.")
  val installPackages = TaskKey[Seq[File]]("install-packages", "Install custom Python packages for Sphinx.")
  val enableOutput = TaskKey[Boolean]("enable-output", "Enable/disable generation of different outputs.")
  val generateHtml = TaskKey[File]("generate-html", "Run Sphinx generation for HTML output.")
  val generatePdf = TaskKey[File]("generate-pdf", "Run Sphinx generation for PDF output.")
  val generatedHtml = TaskKey[Option[File]]("generated-html", "Sphinx HTML output, if enabled. Enabled by default.")
  val generatedPdf = TaskKey[Option[File]]("generated-pdf", "Sphinx PDF output, if enabled. Disabled by default.")
  val generate = TaskKey[File]("generate", "Run all enabled Sphinx generation and combine output.")

  val settings: Seq[Setting[_]] = inConfig(Sphinx)(Seq(
    sourceDirectory <<= sourceDirectory / "sphinx",
    target <<= target / "sphinx",
    sphinxPackages := Seq.empty,
    sphinxTags := Seq.empty,
    sphinxProperties := Map.empty,
    sphinxIncremental := false,
    includeFilter in generate := AllPassFilter,
    excludeFilter in generate := HiddenFileFilter,
    sphinxInputs <<= combineSphinxInputs,
    sphinxRunner := SphinxRunner(),
    installPackages <<= installPackagesTask,
    enableOutput in generateHtml := true,
    enableOutput in generatePdf := false,
    generateHtml <<= generateHtmlTask,
    generatePdf <<= generatePdfTask,
    generatedHtml <<= ifEnabled(generateHtml),
    generatedPdf <<= ifEnabled(generatePdf),
    generate <<= generateTask,
    includeFilter := ("*.html" | "*.pdf" | "*.png" | "*.js" | "*.css" | "*.gif" | "*.txt"),
    mappings <<= mappingsTask,
    // For now, we default to passing the version in as a property.
    sphinxProperties <++= (version apply defaultVersionProperties)
  ))

  
  def defaultVersionProperties(version: String) = {
    val binV = CrossVersion.binaryVersion(version, "")
    Map("version" -> binV, "release" -> version) 
  }

  def installPackagesTask = (sphinxRunner, sphinxPackages, target, streams) map {
    (runner, packages, baseTarget, s) => packages map { p => runner.installPackage(p, baseTarget, s.log) }
  }

  def combineSphinxInputs = {
    (sourceDirectory, includeFilter in generate, excludeFilter in generate, sphinxIncremental, installPackages, sphinxTags, sphinxProperties) map SphinxInputs
  }

  def generateHtmlTask = (sphinxRunner, sphinxInputs, target, cacheDirectory, streams) map {
    (runner, inputs, baseTarget, cacheDir, s) => runner.generateHtml(inputs, baseTarget, cacheDir, s.log)
  }

  def generatePdfTask = (sphinxRunner, sphinxInputs, target, cacheDirectory, streams) map {
    (runner, inputs, baseTarget, cacheDir, s) => runner.generatePdf(inputs, baseTarget, cacheDir, s.log)
  }

  def ifEnabled[T](key: TaskKey[T]): Initialize[Task[Option[T]]] = (enableOutput in key in key.scope, key.task) flatMap {
    (enabled, enabledTask) => if (enabled) (enabledTask map Some.apply) else task { None }
  }

  def generateTask = (generatedHtml, generatedPdf, target, cacheDirectory, streams) map {
    (htmlOutput, pdfOutput, baseTarget, cacheDir, s) => {
      val target = baseTarget / "docs"
      val cache = cacheDir / "sphinx" / "docs"
      val htmlMapping = htmlOutput.toSeq flatMap { html => (html ***).get x rebase(html, target) }
      val pdfMapping = (pdfOutput map { pdf => (pdf, target / pdf.name) }).toSeq
      val mapping = htmlMapping ++ pdfMapping
      Sync(cache)(mapping)
      s.log.info("Sphinx documentation generated: %s" format target)
      target
    }
  }

  def mappingsTask = (generate, includeFilter) map {
    (output, include) => output ** include x relativeTo(output)
  }
}
