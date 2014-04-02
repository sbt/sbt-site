package com.typesafe.sbt
package site

import sbt._
import sbt.Keys._
import sbt.Project.Initialize
import com.typesafe.sbt.sphinx._

object SphinxSupport {
  val Sphinx = config("sphinx")

  val sphinxEnv = TaskKey[Map[String, String]]("sphinx-env", "Environment variables to set for forked sphinx-build process.")
  val sphinxPackages = SettingKey[Seq[File]]("sphinx-packages", "Custom Python package sources to install for Sphinx.")
  val sphinxTags = SettingKey[Seq[String]]("sphinx-tags", "Sphinx tags that should be passed when running Sphinx.")
  val sphinxProperties = SettingKey[Map[String, String]]("sphinx-properties", "-D options that should be passed when running Sphinx.")
  val sphinxIncremental = SettingKey[Boolean]("sphinx-incremental", "Use incremental Sphinx building. Off by default.")
  val sphinxInputs = TaskKey[SphinxInputs]("sphinx-inputs", "Combined inputs for the Sphinx runner.")
  val sphinxRunner = TaskKey[SphinxRunner]("sphinx-runner", "The class used to run Sphinx commands.")
  val installPackages = TaskKey[Seq[File]]("install-packages", "Install custom Python packages for Sphinx.")
  val enableOutput = TaskKey[Boolean]("enable-output", "Enable/disable generation of different outputs.")
  val generateHtml = TaskKey[File]("generate-html", "Run Sphinx generation for HTML output.")
  val generatePdf = TaskKey[Seq[File]]("generate-pdf", "Run Sphinx generation for PDF output.")
  val generateEpub = TaskKey[File]("generate-epub", "Run Sphinx generation for Epub output.")
  val generatedHtml = TaskKey[Option[File]]("generated-html", "Sphinx HTML output, if enabled. Enabled by default.")
  val generatedPdf = TaskKey[Seq[File]]("generated-pdf", "Sphinx PDF output, if enabled. Disabled by default.")
  val generatedEpub = TaskKey[Option[File]]("generated-epub", "Sphinx Epub output, if enabled. Disabled by default")
  val generate = TaskKey[File]("sphinx-generate", "Run all enabled Sphinx generation and combine output.")

  val settings: Seq[Setting[_]] = inConfig(Sphinx)(Seq(
    sourceDirectory <<= sourceDirectory / "sphinx",
    target <<= target / "sphinx",
    sphinxPackages := Seq.empty,
    sphinxTags := Seq.empty,
    sphinxProperties := Map.empty,
    sphinxEnv := Map.empty,
    sphinxIncremental := false,
    includeFilter in generate := AllPassFilter,
    excludeFilter in generate := HiddenFileFilter,
    sphinxInputs <<= combineSphinxInputs,
    sphinxRunner := SphinxRunner(),
    installPackages <<= installPackagesTask,
    enableOutput in generateHtml := true,
    enableOutput in generatePdf := false,
    enableOutput in generateEpub := false,
    generateHtml <<= generateHtmlTask,
    generatePdf <<= generatePdfTask,
    generateEpub <<= generateEpubTask,
    generatedHtml <<= ifEnabled(generateHtml),
    generatedPdf <<= seqIfEnabled(generatePdf),
    generatedEpub <<= ifEnabled(generateEpub),
    generate <<= generateTask,
    includeFilter in Sphinx := AllPassFilter,
    mappings <<= mappingsTask,
    // For now, we default to passing the version in as a property.
    sphinxProperties <++= (version apply defaultVersionProperties),
    sphinxEnv <<= defaultEnvTask
    // TODO - We may want the ~ support to be optional...
  )) ++ Seq(watchSources in Global <++= (sourceDirectory in Sphinx) map (d => d.***.get))

  def defaultEnvTask = installPackages map {
    pkgs =>
      Map(
        "PYTHONPATH" -> Path.makeString(pkgs)
      )
  }
  
  def defaultVersionProperties(version: String) = {
    val binV = CrossVersion.binaryVersion(version, "")
    Map("version" -> binV, "release" -> version) 
  }

  def installPackagesTask = (sphinxRunner, sphinxPackages, target, streams) map {
    (runner, packages, baseTarget, s) => packages map { p => runner.installPackage(p, baseTarget, s.log) }
  }

  def combineSphinxInputs = {
    (sourceDirectory, includeFilter in generate, excludeFilter in generate, sphinxIncremental, sphinxTags, sphinxProperties, sphinxEnv) map SphinxInputs
  }

  def generateHtmlTask = (sphinxRunner, sphinxInputs, target, cacheDirectory, streams) map {
    (runner, inputs, baseTarget, cacheDir, s) => runner.generateHtml(inputs, baseTarget, cacheDir, s.log)
  }

  def generatePdfTask = (sphinxRunner, sphinxInputs, target, cacheDirectory, streams) map {
    (runner, inputs, baseTarget, cacheDir, s) => runner.generatePdf(inputs, baseTarget, cacheDir, s.log)
  }

  def generateEpubTask = (sphinxRunner, sphinxInputs, target, cacheDirectory, streams) map {
    (runner, inputs, baseTarget, cacheDir, s) => runner.generateEpub(inputs, baseTarget, cacheDir, s.log)
  }

  def ifEnabled[T](key: TaskKey[T]): Initialize[Task[Option[T]]] = ifEnabled0[T,Option[T]](key, _ map Some.apply, None)
  def seqIfEnabled[T](key: TaskKey[Seq[T]]): Initialize[Task[Seq[T]]] = ifEnabled0[Seq[T], Seq[T]](key, identity, Nil)

  private[this] def ifEnabled0[S,T](key: TaskKey[S], f: Task[S] => Task[T], nil: T): Initialize[Task[T]] = (enableOutput in key in key.scope, key.task) flatMap {
    (enabled, enabledTask) => if (enabled) f(enabledTask) else task { nil }
  }

  def generateTask = (generatedHtml, generatedPdf, generatedEpub, target, cacheDirectory, streams) map {
    (htmlOutput, pdfOutputs, epubOutput, baseTarget, cacheDir, s) => {
      val target = baseTarget / "docs"
      val cache = cacheDir / "sphinx" / "docs"
      val htmlMapping = htmlOutput.toSeq flatMap { html => (html ***).get x rebase(html, target) }
      val pdfMapping = pdfOutputs map { pdf => (pdf, target / pdf.name) }
      val epubMapping = epubOutput.toSeq flatMap { epub => (epub ** "*.epub").get x rebase(epub, target) }
      val mapping = htmlMapping ++ pdfMapping ++ epubMapping
      Sync(cache)(mapping)
      s.log.info("Sphinx documentation generated: %s" format target)
      target
    }
  }

  def mappingsTask = (generate, includeFilter) map {
    (output, include) => output ** include --- output x relativeTo(output)
  }
}
