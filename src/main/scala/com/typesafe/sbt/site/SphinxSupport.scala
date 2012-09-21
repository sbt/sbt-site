package com.typesafe.sbt
package site

import sbt._
import Keys._
import sphinx._
import SbtSite.SiteKeys.siteMappings


object SphinxSupport {
  val Sphinx = config("sphinx")

  val sphinxConfigSettings = SettingKey[Map[String, String]]("sphinx-config-settings", "-D options that should be passed when running sphinx.")
  val sphinxRunner = TaskKey[SphinxRunner]("sphinx-runner", "A class used to run sphinx commands.")
  val settings: Seq[Setting[_]] =
    Seq(
      sourceDirectory in Sphinx <<= sourceDirectory(_ / "sphinx"),
      target in Sphinx <<= target(_ / "sphinx"),
      // Note: txt is used for search index.
      includeFilter in Sphinx := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "*.txt"),
      // By default, turn off smartypants so we get copy-pastable examples.
      sphinxConfigSettings := Map("html_use_smartypants" -> "0")
    ) ++ inConfig(Sphinx)(Seq(
      sphinxRunner <<= (sphinxConfigSettings) map sphinx.newRunner,
      mappings <<= (sphinxRunner, sourceDirectory, target, includeFilter, streams) map sphinx.generate
    ))
}
