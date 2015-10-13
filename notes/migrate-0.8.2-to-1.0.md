# Migrating `sbt-site` from 0.x to 1.x
The 1.0 release of `sbt-site` marks the conversion to the [`AutoPlugin`](http://www.scala-sbt.org/0.13/docs/Plugins.html) facility, first introduced in sbt 0.13.5.

Because sbt best practices have evolved with the introduction of `AutoPlugin`, this upgrade to `sbt-site` has resulted in a number of "breaking changes" insofar as the way site generation is configured (the same functionality remains).


## Enabling Generators
The biggest of the breaking changes is the use of `enablePlugins(<plugin object>)` instead of calls of the form `site.xyzSupport()`.

The base plugin (`SitePlugin`) is enabled by default, so files in the `src/site` directory are processed according to the default settings. Invocation of other generators must be adjusted accordingly.

Generator   | Previous (0.x)              | New (1.x)
----------- | --------------------------- | -----------------------------------
Base        | `site.settings`             | _automatic_
Preprocess  | `site.preprocessSite()`     | `enablePlugins(PreprocessPlugin)`
Jekyll      | `site.jekyllSupport()`      | `enablePlugins(JekyllPlugin)`
Sphinx      | `site.sphinxSupport()`      | `enablePlugins(SphinxPlugin)`
Pamflet     | `site.pamfletSupport()`     | `enablePlugins(PamfletPlugin)`
Nanoc       | `site.nanocSupport()`       | `enablePlugins(NanocPlugin)`
Asciidoctor | `site.asciidoctorSupport()` | `enablePlugins(AsciidoctorPlugin)`
Scaladoc    | `site.includeScaladoc()`    | `enablePlugins(SiteScaladocPlugin)`

## Configuring Site Subdirectory
Each of the generators can be instructed to populate a subdirectory under the `target/site` destination. This is done by setting the `siteSubdirName` key in the configuration scope of the generator. For example, the following `build.sbt` fragment enables the Jekyll and Scaladoc generators, and configures their output to be `target/site/jekyll-goodness` and `target/site/jekyll-goodness/api`, respectively.

```
enablePlugins(JekyllPlugin, SiteScaladocPlugin)
siteSubdirName in Jekyll := "jekyll-goodness"
siteSubdirName in SiteScaladoc := "jekyll-goodness/api"
```

## Miscellaneous
In the `PreprocessPlugin`, the key `preprocessExts: SettingKey[Set[String]]` has been replaced by `preprocessIncludeFilter: SettingKey[FileFilter]`.

