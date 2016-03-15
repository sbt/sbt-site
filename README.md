# sbt-site
[![Build Status](https://travis-ci.org/sbt/sbt-site.svg)](https://travis-ci.org/sbt/sbt-site)

[![Join the chat at https://gitter.im/sbt/sbt-site](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sbt/sbt-site?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[ ![Download](https://api.bintray.com/packages/sbt/sbt-plugin-releases/sbt-site/images/download.svg) ](https://bintray.com/sbt/sbt-plugin-releases/sbt-site-imported/_latestVersion)

This sbt plugin generates project websites from static content, [Jekyll], [Sphinx], [Pamflet], [Nanoc], [GitBook], and/or [Asciidoctor], and can optionally include generated ScalaDoc. It is designed to work hand-in-hand with publishing plugins like [sbt-ghpages].

**Table of Contents**

- [sbt-site](#sbt-site)
	- [Usage](#usage)
	- [Adding Static Content to Your Site](#adding-static-content-to-your-site)
		- [Static Content with Variable Substitution](#static-content-with-variable-substitution)
		- [Jekyll Site Generation](#jekyll-site-generation)
		- [Sphinx Site Generation](#sphinx-site-generation)
		- [Pamflet Site Generation](#pamflet-site-generation)
		- [Nanoc Site Generation](#nanoc-site-generation)
		- [Asciidoctor Site Generation](#asciidoctor-site-generation)
		- [GitBook Site Generation](#gitbook-site-generation)
	- [ScalaDoc APIs](#scaladoc-apis)
	- [Previewing the Site](#previewing-the-site)
	- [Packaging and Publishing](#packaging-and-publishing)
	- [Advanced Usage](#advanced-usage)
	- [License](#license)
	
## Usage

`sbt-site` is deployed as an `AutoPlugin`. To enable, simply add the following to your `project/plugins.sbt` file:

```
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.0.0")
```

<big>To upgrade from a previous version (e.g. 0.8.x), please see the **[migration guide]**.</big>

_Note_: As of `sbt-site` version 1.0.x, sbt version >= 0.13.5 is required.  

* For earlier 0.13.x releases, use [version 0.8.2][0.8.2].
* For sbt 0.12, use [version 0.7.2][0.7.2].

## Adding Static Content to Your Site
When you run `makeSite`, your project's webpage is generated in the `target/site` directory. By default, all files under `src/site` are included in `target/site`. To use specific third-party generators (e.g. [Jekyll]), additional sub-plugins will need to be enabled, as described below.

The `src/site` directory can be overridden via the `siteSourceDirectory` key:

```
siteSourceDirectory <<= target / "generated-stuff"
```

Additional files outside of `siteSourceDirectory` can be added individually via the `siteMappings` key:

```
siteMappings ++= Seq(file1 -> "location.html", file2 -> "image.png")
```

### Static Content with Variable Substitution
The site plugin supports basic variable substitution when copying files from `src/site-preprocess`. To enable, add this to your `build.sbt` file:

```
enablePlugins(PreprocessPlugin)
```

Variables are delimited by surrounding the name with `@` symbols (e.g. `@VERSION@`). Values are assigned to variables via the setting `preprocessVars: [Map[String, String]]`. For example:

```
preprocessVars := Map("VERSION" -> version.value, "DATE" -> new Date().toString)
```

Note that the plugin will generate an error if a variable is found in the source file with no matching value in `preprocessVars`.

The setting `preprocessIncludeFilter` is used to define the filename extensions that should be processed when `makeSite` is run.

```
preprocessIncludeFilter := "*.md" | "*.markdown"
```

The default filter is `"*.txt" | "*.html" | "*.md" | "*.rst"`.

### Jekyll Site Generation
The `sbt-site` plugin has direct support for running [Jekyll]. This is useful for supporting custom Jekyll plugins that are not allowed when publishing to GitHub, or hosting a Jekyll site on your own server. To add Jekyll support, enable the associated plugin:

```
enablePlugins(JekyllPlugin)
```

This assumes you have a Jekyll project in the `src/jekyll` directory. To change this, set the key `sourceDirectory` in the `Jekyll` scope:

```
sourceDirectory in Jekyll := sourceDirectory / "hyde"
```

To redirect the output to a subdirectory of `target/site`, use the `siteSubdirName` key in `Jekyll` scope:

```
// Puts output in `target/site/notJekyllButHyde`
siteSubdirName in Jekyll := "notJekyllButHyde"
```

One common issue with Jekyll is ensuring that everyone uses the same version for generating a website. There is special support for ensuring the version of gems. To do so, add the following to your `build.sbt` file:

```
requiredGems := Map(
  "jekyll" -> "0.11.2",
  "liquid" -> "2.3.0"
)
```

### Sphinx Site Generation
The `sbt-site` plugin has direct support for building [Sphinx] projects. To enable Sphinx site generation, simply enable the associated plugin in your `build.sbt` file:

```
enablePlugins(SphinxPlugin)
```

This assumes you have a Sphinx project under the `src/sphinx` directory. To change this, set the `sourceDirectory` key in the `Sphinx` scope:

```
sourceDirectory in Sphinx := sourceDirectory / "androsphinx"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Sphinx` scope:

```
// Puts output in `target/site/giza`
siteSubdirName in Sphinx := "giza"
```

### Pamflet Site Generation

The `sbt-site` plugin has direct support for building [Pamflet] projects. To enable Pamflet site generation, simply enable the associated plugin in your `build.sbt` file:

```
enablePlugins(PamfletPlugin)
```

This assumes you have a Pamflet project under the `src/pamflet` directory. To change this, set the `sourceDirectory` key in the `Pamflet` scope:

```
sourceDirectory in Pamflet := sourceDirectory / "papyrus"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Pamflet` scope:

```
// Puts output in `target/site/parchment`
siteSubdirName in Pamflet := "parchment"
```

### Nanoc Site Generation

The `sbt-site` plugin has direct support for building [Nanoc] projects. To enable Nanoc site generation, simply enable the associated plugin in your `build.sbt` file:

```
enablePlugins(NanocPlugin)
```

This assumes you have a Nanoc project under the `src/nanoc` directory. To change this, set the `sourceDirectory` key in the `Nanoc` scope:

```
sourceDirectory in Nanoc := sourceDirectory / "conan"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Nanoc` scope:

```
// Puts output in `target/site/conan`
siteSubdirName in Nanoc := "conan"
```


### Asciidoctor Site Generation
The `sbt-site` plugin has direct support for building [Asciidoctor] projects. To enable Asciidoctor site generation, simply enable the associated plugin in your `build.sbt` file:

```
enablePlugins(AsciidoctorPlugin)
```

This assumes you have an Asciidoctor project under the `src/asciidoctor` directory. To change this, set the `sourceDirectory` key in the `Asciidoctor` scope:

```
sourceDirectory in Asciidoctor := sourceDirectory / "asciimd"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Asciidoctor` scope:

```
// Puts output in `target/site/asciimd`
siteSubdirName in Asciidoctor := "asciimd"
```


### GitBook Site Generation

The `sbt-site` plugin has direct support for building [GitBook] projects. To enable GitBook site generation, simply enable the associated plugin in your `build.sbt` file:

```
enablePlugins(GitBookPlugin)
```

This assumes you have a GitBook project under the `src/gitbook` directory. To change this, set the `sourceDirectory` key in the `GitBook` scope:

```
sourceDirectory in GitBook := sourceDirectory / "doc"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `GitBook` scope:

```
// Puts output in `target/site/book`
siteSubdirName in GitBook := "book"
```

The plugin can also be configured to manage all GitBook setup and installation by configuring a dedicated directory in which GitBook's npm packages can be installed.

```scala
gitbookInstallDir in GitBook := Some(baseDirectory.value / "node_modules" / "gitbook")
```


## ScalaDoc APIs
To include ScalaDoc with your site, add the following line to your `build.sbt`:

```
enablePlugins(SiteScaladocPlugin)
```

This will default to putting the ScalaDoc under the `latest/api` directory on the website. You can change this with the `siteSubdirName` key in the `SiteScaladoc` scope:

```
// Puts ScalaDoc output in `target/set/api/wip`
siteSubdirName in SiteScaladoc := "api/wip"
```

## Publishing to Github Pages

See the [sbt-ghpages] plugin for information about publishing to [GitHub Pages]. We expect other publishing mechanisms to be supported in the future.

## Previewing the Site
To preview your generated site, run `previewSite`, which launches a web server on port 4000 and attempts to connect your browser to [http://localhost:4000/](http://localhost:4000/). To change the server port, use the key `previewFixedPort`:

```
previewFixedPort := Some(9999)
```

To disable browser auto-open, use the key `previewLaunchBrowser`:

```
previewLaunchBrowser := false
```

## Packaging and Publishing
To create a zip package of the site run `package-site`.

To also include this zip file as an artifact when running `publish`, add the following to your `build.sbt`:

```
publishSite
```

See the [`sbt-ghpages`](sbt-ghpages) plugin documentation for simplified publishing to [GitHub Pages].

## Advanced Usage

If you need to run a generator on more than one source directory, bypassing the `AutoPlugin` system and defining one or more sbt `Configuration`s is necessary. For example, suppose you two Pamflet source directories and want them each generated as a subdirectory under `target/site`. A `build.sbt` might look something like this:

```
// Define two `Configuration` instances.
val Site1 = config("site1")

val Site2 = config("site2")

// Apply the default pamflet settings to the `Site1` config
PamfletPlugin.pamfletSettings(Site1)

// Customize the source directory
sourceDirectory in Site1 := sourceDirectory.value / "pamflet-site-1"

// Customize the output subdirectory
siteSubdirName in Site1 := "chapter1"

// Same as above, but for config `Site2`
PamfletPlugin.pamfletSettings(Site2)

sourceDirectory in Site2 := sourceDirectory.value / "pamflet-site-2"

siteSubdirName in Site2 := "chapter2"
```

Each of the other generators follow a similar pattern (e.g. `JekyllPlugin.jekyllSettings(config("foo"))`).

## License

`sbt-site` is released under a "BSD 3-Clause" license. See [LICENSE](LICENSE) for specifics and copyright declaration.

[0.7.2]: https://github.com/sbt/sbt-site/tree/v0.7.2
[0.8.2]: https://github.com/sbt/sbt-site/tree/v0.8.2
[migration guide]: notes/migrate-0.8.2-to-1.0.md
[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
[jekyll]: http://jekyllrb.com
[pamflet]: http://pamflet.databinder.net
[nanoc]: http://nanoc.ws/
[asciidoctor]: http://asciidoctor.org
[gitbook]: https://help.gitbook.com/
[sphinx]: http://sphinx-doc.org
[GitHub Pages]: https://pages.github.com
[GitBook]: https://www.gitbook.com
