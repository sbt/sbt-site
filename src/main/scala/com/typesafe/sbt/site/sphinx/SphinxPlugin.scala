package com.typesafe.sbt.site.sphinx

import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._
/** Sphinx generator. */
object SphinxPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger

  object autoImport extends SphinxKeys {
    val Sphinx = config("sphinx")
  }
  import autoImport._
  override def projectSettings = sphinxSettings(Sphinx)

  def sphinxSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        sphinxPackages := Seq.empty,
        sphinxTags := Seq.empty,
        sphinxProperties := Map.empty,
        sphinxEnv := Map.empty,
        sphinxIncremental := false,
        includeFilter in generate := AllPassFilter,
        excludeFilter in generate := HiddenFileFilter,
        sphinxInputs := combineSphinxInputs.value,
        sphinxRunner := SphinxRunner(),
        installPackages := installPackagesTask.value,
        enableOutput in generateHtml := true,
        enableOutput in generatePdf := false,
        enableOutput in generateEpub := false,
        generateHtml := generateHtmlTask.value,
        generatePdf := generatePdfTask.value,
        generateEpub := generateEpubTask.value,
        generatedHtml := ifEnabled(generateHtml).value,
        generatedPdf := seqIfEnabled(generatePdf).value,
        generatedEpub := ifEnabled(generateEpub).value,
        generate := generateTask.value,
        includeFilter in Sphinx := AllPassFilter,
        mappings := mappingsTask.value,
        // For now, we default to passing the version in as a property.
        sphinxProperties ++= defaultVersionProperties(version.value),
        sphinxEnv := defaultEnvTask.value,
        siteSubdirName := ""
      )
    ) ++
      SiteHelpers.directorySettings(config) ++
      SiteHelpers.watchSettings(config) ++
      SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)

  def defaultEnvTask = installPackages map {
    pkgs => Map("PYTHONPATH" -> Path.makeString(pkgs))
  }

  def defaultVersionProperties(version: String) = {
    val binV = CrossVersion.binaryVersion(version, "")
    Map("version" -> binV, "release" -> version)
  }

  def installPackagesTask = Def.task {
    val runner = sphinxRunner.value
    val packages = sphinxPackages.value
    val s = streams.value
    packages map { p => runner.installPackage(p, target.value, s.log) }
  }

  def combineSphinxInputs = Def.task {
    SphinxInputs(
      sourceDirectory.value,
      (includeFilter in generate).value,
      (excludeFilter in generate).value,
      sphinxIncremental.value,
      sphinxTags.value,
      sphinxProperties.value,
      sphinxEnv.value
    )
  }

  def generateHtmlTask = Def.task {
    val runner = sphinxRunner.value
    val inputs = sphinxInputs.value
    val t = target.value
    val s = streams.value
    runner.generateHtml(inputs, t, s.cacheDirectory, s.log)
  }

  def generatePdfTask = Def.task {
    val runner = sphinxRunner.value
    val inputs = sphinxInputs.value
    val t = target.value
    val s = streams.value
    runner.generatePdf(inputs, t, s.cacheDirectory, s.log)
  }

  def generateEpubTask = Def.task {
    val runner = sphinxRunner.value
    val inputs = sphinxInputs.value
    val t = target.value
    val s = streams.value
    runner.generateEpub(inputs, t, s.cacheDirectory, s.log)
  }

  def ifEnabled[T](key: TaskKey[T]): Def.Initialize[Task[Option[T]]] = ifEnabled0[T, Option[T]](
    key, _ map Some.apply, None)
  def seqIfEnabled[T](key: TaskKey[Seq[T]]): Def.Initialize[Task[Seq[T]]] = ifEnabled0[Seq[T], Seq[T]](
    key, identity, Nil)

  private[this] def ifEnabled0[S, T](
    key: TaskKey[S],
    f: Task[S] => Task[T],
    nil: T): Def.Initialize[Task[T]] = Def.task{
    if ((enableOutput in key in key.scope).value) f(key.taskValue) else task {nil}
  }.flatMap(identity)

  def generateTask = Def.task {
    val htmlOutput = generatedHtml.value
    val pdfOutputs = generatedPdf.value
    val epubOutput = generatedEpub.value
    val s = streams.value
    val cacheDir = s.cacheDirectory
    val t = target.value / "docs"
    val cache = cacheDir / "sphinx" / "docs"
    val htmlMapping = htmlOutput.toSeq flatMap { html =>
      (html ** AllPassFilter).get pair rebase(html, t)
    }
    val pdfMapping = pdfOutputs map { pdf => (pdf, t / pdf.name) }
    val epubMapping = epubOutput.toSeq flatMap { epub =>
      (epub ** "*.epub").get pair rebase(epub, t)
    }
    val mapping = htmlMapping ++ pdfMapping ++ epubMapping
    Sync(cache)(mapping)
    s.log.info("Sphinx documentation generated: %s" format t)
    t
  }

  def mappingsTask = Def.task {
    val output = generate.value
    val include = includeFilter.value
    output ** include --- output pair relativeTo(output)
  }
}
