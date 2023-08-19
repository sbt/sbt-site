package com.typesafe.sbt.site.asciidoctor

import sbt.SettingKey

trait AsciidoctorKeys {
  val asciidoctorAttributes = SettingKey[Map[String, String]](
    "asciidoctor-attributes",
    "Asciidoctor allows the rendering process to be influenced by so-called attributes. " +
      "You can read more about attributes here: https://asciidoctor.org/docs/user-manual/#attributes"
  )
}
