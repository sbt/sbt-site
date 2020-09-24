# Asciidoctor

The sbt-site plugin has direct support for building [Asciidoctor] projects. To enable Asciidoctor site generation, simply enable the associated plugin in your `build.sbt` file:

@@ snip[enablePlugin](/src/sbt-test/asciidoctor/can-use-asciidoctor/build.sbt) { #enablePlugin }

This assumes you have an Asciidoctor project under the `src/asciidoctor` directory. To change this, set the `sourceDirectory` key in the `Asciidoctor` scope:

```sbt
Asciidoctor / sourceDirectory := sourceDirectory.value / "asciimd"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Asciidoctor` scope:

@@ snip[siteSubdirName](/src/sbt-test/asciidoctor/can-use-asciidoctor/build.sbt) { #siteSubdirName }

## Attributes

Asciidoctor allows the rendering of documents to be inflenced by [attributes](https://asciidoctor.org/docs/user-manual/#attributes).

You can configure attributes by setting `asciidoctorAttributes`.

```sbt
asciidoctorAttributes := Map(
  "skip-front-matter" -> "", // attribute without value can be set to empty string
  "lang" -> "nl" // attribute with value
)
```

[Asciidoctor]: http://asciidoctor.org
