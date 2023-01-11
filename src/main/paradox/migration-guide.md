# Migration Guide

## Migrating from version 1.4.x to 1.5.x

To cut down on dependencies, sbt-site is now spilt into separate modules per site generator. Three generators are available as specific sbt plugins:
* `addSbtPlugin("com.github.sbt" % "sbt-site-asciidoctor" % "1.5.0")`
* `addSbtPlugin("com.github.sbt" % "sbt-site-gitbook" % "1.5.0")`
* `addSbtPlugin("com.github.sbt" % "sbt-site-paradox" % "1.5.0")`

**The integrations with Jekyll, Hugo, Sphinx, Pamflet, and Nanoc are not available with sbt-site 1.5.0 (see ["revive" issues](https://github.com/sbt/sbt-site/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+revive)).**

The `previewSite` plugin does not enable itself anymore, you need to explicitly enable the parts of sbt-site you need on the sbt project/module containing the documentation. To allow `previewSite` with Paradox for example
```scala
.enablePlugins(SitePreviewPlugin, ParadoxSitePlugin)
```

## Migrating from version 1.3.x to 1.4.x

Only sbt >1.1.6 is supported.

### ParadoxSitePlugin

`ParadoxPlugin` is enabled whenever `ParadoxSitePlugin` is enabled.

Paradox integration now uses the same configuration scopes as the `sbt-paradox` plugin.
Therefore when upgrading remove the `Paradox` configuration scope from all of the `sbt-paradox` settings.
Note that sbt-site setting `siteSubdirName` is still configured under the `Paradox` configuration scope.

The default Paradox source directory has been changed to match the one defined in the `sbt-paradox`, which is `src/main/paradox`.
To keep the previous default, add the following to the build: `paradox / sourceDirectory := sourceDirectory.value`

[Preprocess]: preprocess.md#substitution
[Jekyll]: generators/jekyll.md
[Sphinx]: generators/sphinx.md
[Pamflet]: generators/pamflet.md
[Nanoc]: generators/nanoc.md
[Asciidoctor]: generators/asciidoctor.md
[Scaladoc]: api-documentation.md#scaladoc
