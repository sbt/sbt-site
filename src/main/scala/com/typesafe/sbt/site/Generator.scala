package com.typesafe.sbt
package site

import sbt._
import Keys._

object Generator {
  def directorySettings(config: Configuration): Seq[Setting[_]] =
  	inConfig(config)(Seq(
  		sourceDirectory := sourceDirectory.value / config.name,
      target := target.value / config.name
  	))
  def watchSettings(config: Configuration): Seq[Setting[_]] =
  	Seq(
  		watchSources in Global <++= (sourceDirectory in config) map { _.***.get }
  	)
}
