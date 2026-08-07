package com.typesafe.sbt.site.sphinx

import com.typesafe.sbt.site.Compat._
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._
import sbtcompat.PluginCompat
import sbtcompat.PluginCompat.{ *, given }
/** Sphinx generator. */
object SphinxPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger

  object autoImport extends SphinxKeys {
    val Sphinx = config("sphinx")
  }
  import autoImport._
  override def projectSettings = sphinxSettings(Sphinx)

  def sphinxSettings(config: Configuration): Seq[Setting[?]] =
    inConfig(config)(
      Seq(
        sphinxPackages := Seq.empty,
        sphinxTags := Seq.empty,
        sphinxProperties := Map.empty,
        sphinxEnv := Def.uncached(Map.empty),
        sphinxIncremental := false,
        generate / includeFilter := AllPassFilter,
        generate / excludeFilter := HiddenFileFilter,
        sphinxInputs := Def.uncached(combineSphinxInputs.value),
        sphinxRunner := Def.uncached(SphinxRunner()),
        installPackages := Def.uncached(installPackagesTask.value),
        generateHtml / enableOutput := true,
        generatePdf / enableOutput := false,
        generateEpub / enableOutput := false,
        generateHtml := Def.uncached(generateHtmlTask.value),
        generatePdf := Def.uncached(generatePdfTask.value),
        generateEpub := Def.uncached(generateEpubTask.value),
        generatedHtml := Def.uncached(ifEnabled(generateHtml).value),
        generatedPdf := Def.uncached(seqIfEnabled(generatePdf).value),
        generatedEpub := Def.uncached(ifEnabled(generateEpub).value),
        generate := Def.uncached(generateTask.value),
        Sphinx / includeFilter := AllPassFilter,
        mappings := Def.uncached {
          implicit val conv: xsbti.FileConverter = fileConverter.value
          PluginCompat.toFileRefsMapping(mappingsTask.value)
        },
        version := SiteHelpers.shortVersion(version.value),
        sphinxEnv := Def.uncached(defaultEnvTask.value),
        siteSubdirName := ""
      )
    ) ++
      propertiesSettings(config) ++
      SiteHelpers.directorySettings(config) ++
      SiteHelpers.watchSettings(config) ++
      SiteHelpers.addMappingsToSiteDir(config / mappings, config / siteSubdirName)

  def defaultEnvTask = installPackages map {
    pkgs => Map("PYTHONPATH" -> Path.makeString(pkgs))
  }

  // For now, we default to passing the version in as a property.
  def propertiesSettings(config: Configuration) = Seq(
    config / sphinxProperties ++= Map(
      "version" → (config / version).value,
      "release" → version.value
    )
  )

  def installPackagesTask = Def.task {
    val runner = sphinxRunner.value
    val packages = sphinxPackages.value
    val s = streams.value
    val t = target.value
    packages map { p => runner.installPackage(p, t, s.log) }
  }

  def combineSphinxInputs = Def.task {
    SphinxInputs(
      sourceDirectory.value,
      (generate / includeFilter).value,
      (generate / excludeFilter).value,
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
    nil: T): Def.Initialize[Task[T]] = Def.taskDyn {
    val t = key.taskValue
    if ((key.scope / key / enableOutput).value) Def.value(f(t)) else Def.task { nil }
  }

  def generateTask = Def.task {
    val htmlOutput = generatedHtml.value
    val pdfOutputs = generatedPdf.value
    val epubOutput = generatedEpub.value
    val s = streams.value
    val cacheDir = s.cacheDirectory
    val t = target.value / "docs"
    val cache = cacheDir / "sphinx" / "docs"
    val htmlMapping = htmlOutput.toSeq flatMap { html =>
      (html ** AllPassFilter).get() pair Path.rebase(html, t)
    }
    val pdfMapping = pdfOutputs map { pdf => (pdf, t / pdf.getName) }
    val epubMapping = epubOutput.toSeq flatMap { epub =>
      (epub ** "*.epub").get() pair Path.rebase(epub, t)
    }
    val mapping = htmlMapping ++ pdfMapping ++ epubMapping
    Sync.sync(CacheStore(cache))(mapping)
    s.log.info("Sphinx documentation generated: %s" format t)
    t
  }

  def mappingsTask = Def.task {
    val output = generate.value
    val include = includeFilter.value
    output ** include --- output pair Path.relativeTo(output)
  }
}
