package com.typesafe.sbt.site.sphinx

import sbt._
/**
 * Keys for sphinx website generator.
 * @since 10/29/15
 */
trait SphinxKeys {
  val sphinxEnv = TaskKey[Map[String, String]](
      "sphinx-env", "Environment variables to set for forked sphinx-build process.")
  val sphinxPackages = SettingKey[Seq[File]](
      "sphinx-packages", "Custom Python package sources to install for Sphinx.")
  val sphinxTags = SettingKey[Seq[String]](
      "sphinx-tags", "Sphinx tags that should be passed when running Sphinx.")
  val sphinxProperties = SettingKey[Map[String, String]](
      "sphinx-properties", "-D options that should be passed when running Sphinx.")
  val sphinxIncremental = SettingKey[Boolean](
      "sphinx-incremental", "Use incremental Sphinx building. Off by default.")
  val sphinxInputs = TaskKey[SphinxInputs](
      "sphinx-inputs", "Combined inputs for the Sphinx runner.")
  val sphinxRunner = TaskKey[SphinxRunner](
      "sphinx-runner", "The class used to run Sphinx commands.")
  val installPackages = TaskKey[Seq[File]](
      "install-packages", "Install custom Python packages for Sphinx.")
  val enableOutput = TaskKey[Boolean](
      "enable-output", "Enable/disable generation of different outputs.")
  val generateHtml = TaskKey[File]("generate-html", "Run Sphinx generation for HTML output.")
  val generatePdf = TaskKey[Seq[File]]("generate-pdf", "Run Sphinx generation for PDF output.")
  val generateEpub = TaskKey[File]("generate-epub", "Run Sphinx generation for Epub output.")
  val generatedHtml = TaskKey[Option[File]](
      "generated-html", "Sphinx HTML output, if enabled. Enabled by default.")
  val generatedPdf = TaskKey[Seq[File]](
      "generated-pdf", "Sphinx PDF output, if enabled. Disabled by default.")
  val generatedEpub = TaskKey[Option[File]](
      "generated-epub", "Sphinx Epub output, if enabled. Disabled by default")
  val generate = TaskKey[File](
      "sphinx-generate", "Run all enabled Sphinx generation and combine output.")
}
