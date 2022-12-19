# Migration Guide

<a id="from-version-1.3.x-to-1.4.x" />
## Migrating from version 1.3.x to 1.4.x

Only sbt >1.1.6 is supported.

### ParadoxSitePlugin

`ParadoxPlugin` is enabled whenever `ParadoxSitePlugin` is enabled.

Paradox integration now uses the same configuration scopes as the `sbt-paradox` plugin.
Therefore when upgrading remove the `Paradox` configuration scope from all of the `sbt-paradox` settings.
Note that sbt-site setting `siteSubdirName` is still configured under the `Paradox` configuration scope.

The default Paradox source directory has been changed to match the one defined in the `sbt-paradox`, which is `src/main/paradox`.
To keep the previous default, add the following to the build: `paradox / sourceDirectory := sourceDirectory.value`

<a id="from-version-0.x.x-to-1.x.x" />
## Migrating from version 0.x.x to 1.x.x

The 1.x.x release of sbt-site marks the conversion to the [`AutoPlugin`](https://www.scala-sbt.org/0.13/docs/Plugins.html) facility, first introduced in sbt 0.13.5.

Because sbt best practices have evolved with the introduction of `AutoPlugin`, this upgrade to sbt-site has resulted in a number of "breaking changes" insofar as the way site generation is configured (the same functionality remains).

### Enabling Generators

The biggest of the breaking changes is the use of `enablePlugins(<plugin object>)` instead of calls of the form `site.xyzSupport()`.

The base plugin (`SitePlugin`) is enabled by default, so files in the `src/site` directory are processed according to the default settings. Invocation of other generators must be adjusted accordingly.

Generator          | Previous (0.x)              | New (1.x)
------------------ | --------------------------- | -----------------------------------
Base               | `site.settings`             | _automatic_
@ref:[Preprocess]  | `site.preprocessSite()`     | `enablePlugins(PreprocessPlugin)`
@ref:[Jekyll]      | `site.jekyllSupport()`      | `enablePlugins(JekyllPlugin)`
@ref:[Sphinx]      | `site.sphinxSupport()`      | `enablePlugins(SphinxPlugin)`
@ref:[Pamflet]     | `site.pamfletSupport()`     | `enablePlugins(PamfletPlugin)`
@ref:[Nanoc]       | `site.nanocSupport()`       | `enablePlugins(NanocPlugin)`
@ref:[Asciidoctor] | `site.asciidoctorSupport()` | `enablePlugins(AsciidoctorPlugin)`
@ref:[Scaladoc]    | `site.includeScaladoc()`    | `enablePlugins(SiteScaladocPlugin)`

### Configuring Site Subdirectory

Each of the generators can be instructed to populate a subdirectory under the `target/site` destination. This is done by setting the `siteSubdirName` key in the configuration scope of the generator. For example, the following `build.sbt` fragment enables the Jekyll and Scaladoc generators, and configures their output to be `target/site/jekyll-goodness` and `target/site/jekyll-goodness/api`, respectively.

```sbt
enablePlugins(JekyllPlugin, SiteScaladocPlugin)
siteSubdirName in Jekyll := "jekyll-goodness"
siteSubdirName in SiteScaladoc := "jekyll-goodness/api"
```

### Adding Custom Mappings to a Site Directory

Content added using:

```scala
site.addMappingsToSiteDir(mappings: TaskKey[Seq[(File,String)]], nestedDirectory: String)
```

has been replaced with:

```scala
addMappingsToSiteDir(mappings: TaskKey[Seq[(File, String)]], nestedDirectory: SettingKey[String])
```

The following examples show how you can define nested directory via a custom setting or by scoping `siteSubdirName` to either an sbt key or configuration:

@@ snip[addMappingsToSiteDir](../../sbt-test/site/can-have-custom-mappings/build.sbt) { #addMappingsToSiteDir }

### Miscellaneous

In the `PreprocessPlugin`, the key `preprocessExts: SettingKey[Set[String]]` has been replaced by `preprocessIncludeFilter: SettingKey[FileFilter]`.

[Preprocess]: preprocess.md#substitution
[Jekyll]: generators/jekyll.md
[Sphinx]: generators/sphinx.md
[Pamflet]: generators/pamflet.md
[Nanoc]: generators/nanoc.md
[Asciidoctor]: generators/asciidoctor.md
[Scaladoc]: api-documentation.md#scaladoc
