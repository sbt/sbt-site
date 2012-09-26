package com.typesafe.sbt
package site

import sbt._
import Keys._
import sphinx._
import SbtSite.SiteKeys.siteMappings


object SphinxSupport {
  val Sphinx = config("sphinx")

  val sphinxPackages = SettingKey[Seq[File]]("sphinx-packages", "Custom Python package sources to install for Sphinx.")
  val sphinxTags = SettingKey[Seq[String]]("sphinx-tags", "Sphinx tags that should be passed when running Sphinx.")
  val sphinxProperties = SettingKey[Map[String, String]]("sphinx-properties", "-D options that should be passed when running Sphinx.")
  val sphinxInputs = TaskKey[SphinxInputs]("sphinx-inputs", "Combined inputs for the Sphinx runner.")
  val sphinxRunner = TaskKey[SphinxRunner]("sphinx-runner", "The class used to run Sphinx commands.")

  val installPackages = TaskKey[Seq[File]]("install-packages", "Install custom Python packages for Sphinx.")
  val enable = TaskKey[Boolean]("enable", "Enable/disable generation of different outputs.")
  val generateHtml = TaskKey[Option[File]]("generate-html", "Run Sphinx generation for HTML output.")
  val generatePdf = TaskKey[Option[File]]("generate-pdf", "Run Sphinx generation for PDF output.")
  val generate = TaskKey[File]("generate", "Run all enabled Sphinx generation and combine output.")

  val settings: Seq[Setting[_]] = inConfig(Sphinx)(Seq(
    sourceDirectory <<= sourceDirectory / "sphinx",
    target <<= target / "sphinx",
    sphinxPackages := Seq.empty,
    sphinxTags := Seq.empty,
    sphinxProperties := Map.empty,
    includeFilter in generate := AllPassFilter,
    excludeFilter in generate := HiddenFileFilter,
    sphinxInputs <<= combineSphinxInputs,
    sphinxRunner := SphinxRunner(),
    installPackages <<= installPackagesTask,
    enable in generateHtml := true,
    enable in generatePdf := false,
    generateHtml <<= generateHtmlTask,
    generatePdf <<= generatePdfTask,
    generate <<= generateTask,
    includeFilter := ("*.html" | "*.pdf" | "*.png" | "*.js" | "*.css" | "*.gif" | "*.txt"),
    mappings <<= mappingsTask
  ))

  def installPackagesTask = (sphinxRunner, sphinxPackages, target, streams) map {
    (runner, packages, baseTarget, s) => packages map { p => runner.installPackage(p, baseTarget, s.log) }
  }

  def combineSphinxInputs = {
    (sourceDirectory, includeFilter in generate, excludeFilter in generate, installPackages, sphinxTags, sphinxProperties) map SphinxInputs
  }

  def generateHtmlTask = (enable in generateHtml, sphinxRunner, sphinxInputs, target, cacheDirectory, streams) map {
    (enabled, runner, inputs, baseTarget, cacheDir, s) => {
      if (enabled) Some(runner.generateHtml(inputs, baseTarget, cacheDir, s.log)) else None
    }
  }

  def generatePdfTask = (enable in generatePdf, sphinxRunner, sphinxInputs, target, cacheDirectory, streams) map {
    (enabled, runner, inputs, baseTarget, cacheDir, s) => {
      if (enabled) Some(runner.generatePdf(inputs, baseTarget, cacheDir, s.log)) else None
    }
  }

  def generateTask = (generateHtml, generatePdf, target, cacheDirectory, streams) map {
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
