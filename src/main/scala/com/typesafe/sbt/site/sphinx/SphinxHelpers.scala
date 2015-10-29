package com.typesafe.sbt.site.sphinx

import sbt._


/**
 * Combined inputs for Sphinx runner.
 */
case class SphinxInputs(src: File, include: FileFilter, exclude: FileFilter, incremental: Boolean, tags: Seq[String], properties: Map[String, String], env: Map[String, String])

object SphinxRunner {
  /**
   * Create the default Sphinx runner.
   */
  def apply(): SphinxRunner = new CommandLineSphinxRunner
}

/**
 * Stable interface for Sphinx runner.
 */
trait SphinxRunner {
  /**
   * Install custom Python package.
   * @return install location
   */
  def installPackage(src: File, target: File, log: Logger): File

  /**
   * Generate HTML output from reStructuedText sources.
   * @return HTML output directory
   */
  def generateHtml(inputs: SphinxInputs, target: File, cacheDir: File, log: Logger): File

  /**
   * Generate PDF output from reStructuedText sources.
   * @return PDF output files
   */
  def generatePdf(inputs: SphinxInputs, target: File, cacheDir: File, log: Logger): Seq[File]

  /**
   * Generate Epub output from reStructuredText sources.
   * @return Epub output files
   */
  def generateEpub(inputs: SphinxInputs, target: File, cacheDir: File, log: Logger): File
}

/**
 * Sphinx runner that calls out to command-line tools: sphinx-build, easy_install, make.
 */
private[sphinx] class CommandLineSphinxRunner extends SphinxRunner {
  def installPackage(src: File, target: File, log: Logger): File = {
    easyInstall(src, target, log)
  }

  def generateHtml(inputs: SphinxInputs, target: File, cacheDir: File, log: Logger): File = {
    sphinxBuild("html", inputs.src, inputs.include, inputs.exclude, target, cacheDir, inputs.incremental, inputs.tags, inputs.properties, inputs.env, log)
  }

  def generatePdf(inputs: SphinxInputs, target: File, cacheDir: File, log: Logger): Seq[File] = {
    val latexOutput = sphinxBuild("latex", inputs.src, inputs.include, inputs.exclude, target, cacheDir, inputs.incremental, inputs.tags, inputs.properties, inputs.env, log)
    makePdf(latexOutput, log)
  }

  def generateEpub(inputs: SphinxInputs, target: File, cacheDir: File, log: Logger): File = {
    sphinxBuild("epub", inputs.src, inputs.include, inputs.exclude, target, cacheDir, inputs.incremental, inputs.tags, inputs.properties, inputs.env, log)
  }

  /**
   * Run easy_install to install a Python package from source.
   * @return install location
   */
  private def easyInstall(src: File, baseTarget: File, log: Logger): File = {
    val name = src.getName
    val target = baseTarget / "packages" / name
    val empty = (target * "*.egg").get.isEmpty
    if (empty) {
      log.info("Installing Sphinx custom package '%s'..." format name)
      IO.withTemporaryDirectory { tmp =>
        IO.copyDirectory(src, tmp)
        target.mkdirs()
        val logger = sphinxLogger(log)
        val command = Seq("easy_install", "--install-dir", target.absolutePath, tmp.absolutePath)
        val env = "PYTHONPATH" -> target.absolutePath
        log.debug("Command: " + command.mkString(" "))
        log.debug("Environment: " + env)
        val exitCode = Process(command, tmp, env) ! logger
        if (exitCode != 0) sys.error("Failed to install Sphinx custom package")
        log.info("Sphinx custom package installed: " + target)
      }
    }
    target
  }

  /**
   * Run sphinx-build with a given builder (html or latex).
   * @return output directory
   */
  private def sphinxBuild(builder: String, src: File, include: FileFilter, exclude: FileFilter, baseTarget: File, cacheDir: File, incremental: Boolean, tags: Seq[String], properties: Map[String, String], env: Map[String,String], log: Logger): File = {
    val target = baseTarget / builder
    val doctrees = baseTarget / "doctrees" / builder
    val cache = cacheDir / "sphinx" / builder
    val cached = FileFunction.cached(cache)(FilesInfo.hash, FilesInfo.exists) { (in, out) =>
      val changes = in.modified
      if (changes.nonEmpty) {
        val tagList = if (tags.isEmpty) "" else tags.mkString(" (", ", ", ")")
        val desc = "%s%s" format (builder, tagList)
        log.info("Generating Sphinx %s documentation..." format desc)
        if (!incremental) IO.delete(target)
        val logger = sphinxLogger(log)
        val buildOptions = if (!incremental) Seq("-a", "-E") else Seq.empty[String]
        val colourOptions = if (!log.ansiCodesSupported) Seq("-N") else Seq.empty[String]
        val tagOptions = tags flatMap (Seq("-t", _))
        val propertyOptions = (properties map { case (k, v) => "-D%s=%s" format (k, v) }).toSeq
        val command = Seq("sphinx-build") ++ buildOptions ++ colourOptions ++ Seq("-b", builder, "-d", doctrees.absolutePath) ++ tagOptions ++ propertyOptions ++ Seq(src.absolutePath, target.absolutePath)
        log.debug("Command: " + command.mkString(" "))
        log.debug("Environment: " + env)
        val exitCode = Process(command, src, env.toSeq : _*) ! logger
        if (exitCode != 0) sys.error("Failed to build Sphinx %s documentation." format desc)
        log.info("Sphinx %s documentation generated: %s" format (desc, target))
        (target ***).get.toSet
      } else Set.empty
    }
    val inputs = src.descendantsExcept(include, exclude).get.toSet
    cached(inputs)
    target
  }

  /**
   * Run make pdf in the latex output directory.
   * @return PDF file
   */
  private def makePdf(latexBase: File, log: Logger): Seq[File] = {
    val texFiles = (latexBase * "*.tex").get
    val pdfFiles = texFiles map { tex =>
      val (base, ext) = tex.baseAndExt
      latexBase / (base + ".pdf")
    }
    val outofdate = (texFiles, pdfFiles).zipped.exists { case (tex, pdf) => tex.lastModified > pdf.lastModified }
    if(outofdate) {
      log.info("Generating Sphinx pdf documentation...")
      val logger = sphinxLogger(log)
      val exitCode = Process(Seq("make", "all-pdf"), latexBase) ! logger
      if (exitCode != 0)
        sys.error("Sphinx pdf generation failed.  See debug output for details.")
      log.info("Sphinx pdf documentation generated:\n\t%s" format pdfFiles.mkString("\n\t"))
    }
    pdfFiles
  }

  /**
   * Create a process logger for filtering sphinx build output.
   */
  private def sphinxLogger(log: Logger) = {
    new ProcessLogger {
      def info(i: => String): Unit = redirect(i)
      def error(e: => String): Unit = redirect(e)
      def buffer[T](f: => T): T = f
      def redirect(message: String): Unit = {
        if (message contains "ERROR") log.error(message)
        else if (message contains "WARNING") log.warn(message)
        else log.debug(message)
      }
    }
  }

}
