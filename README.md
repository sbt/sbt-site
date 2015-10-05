# sbt-site
[![Build Status](https://travis-ci.org/sbt/sbt-site.svg)](https://travis-ci.org/sbt/sbt-site)

[![Join the chat at https://gitter.im/sbt/sbt-site](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sbt/sbt-site?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This is an sbt plugin that can generate project websites.

It is designed to work hand-in-hand with publishing plugins like [sbt-ghpages].

## Usage
Add this to your `project/plugins.sbt`:

```
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")
```

_Note: this requires sbt 0.13 - for sbt 0.12 see the [0.7.2 version][old] of this plugin._

And then in your `build.sbt` file:

```
site.settings
```

Then, run `make-site` to generate your project's webpage in the `target/site` directory.

See the [sbt-ghpages] plugin for information about publishing to gh-pages. We expect other publishing mechanisms to be supported in the future.

## Adding Content to Your Site
The sbt-site plugin has specific "support" settings for the various supported ways of generating a site. By default, all files under `src/site` are included in the site. You can add new generated content in one of the following ways:

### Static Site
Simply add to the site mappings. In your `build.sbt` file:

```
site.siteMappings <++= Seq(file1 -> "location.html", file2 -> "image.png")
```

### Changing the Source Directory
To change the source directory for static site files, use the `siteSourceDirectory` alias:

```
siteSourceDirectory <<= target / "generated-stuff"
```

### Static Content with Variable Substitution
The site plugin supports basic variable substitution when copying files from `src/site-preprocess`. To enable, add this to your `build.sbt` file:

```
site.preprocessSite()
```

Variables are delimited with two `@` symbols, e.g. `@VERSION@`. Variables are defined via the `preprocessVars[Map[String, String]]` setting. For example:

```
preprocessVars := Map("VERSION" -> version.value, "DATE" -> new Date().toString)
```

The setting `preprocessExt[Set[String]]` is used to define the filename extensions that should be processed when `make-site` is run. The defaults are `Set("txt", "html", "md")`

> Note: The extension specifications must not include the final dot (`.`). Only the symbols after the dot.

### Jekyll Site Generation
The site plugin has direct support for running [jekyll][jekyll] locally.  This is suprisingly useful for suporting custom jekyll plugins that are not allowed when publishing to github, or hosting a jekyll site on your own server. To add jekyll support, add the following to your `build.sbt`:

```
site.jekyllSupport()
```

This assumes you have a jekyll project in the `src/jekyll` directory.

One common issue with Jekyll is ensuring that everyone uses the same version for generating a website.  There is special support for ensuring the version of gems. To do so, add the following to your `build.sbt` file:

```
com.typesafe.sbt.site.JekyllSupport.requiredGems := Map(
  "jekyll" -> "0.11.2",
  "liquid" -> "2.3.0"
)
```

### Sphinx Site Generation
The site plugin has direct support for building [Sphinx][sphinx] projects locally. This assumes you have a sphinx project under the `src/sphinx` directory. To enable sphinx site generation, simply add the following to your `build.sbt` file:

```
site.sphinxSupport()
```

### Pamflet Site Generation
The site plugin has direct support for building [Pamflet](http://pamflet.databinder.net/) projects.   This assumes you have a pamflet project under the `src/pamflet` directory.   To enable pamflet site generation, simply add the following to your `build.sbt` file:

```
site.pamfletSupport()
```

To place pamflet HTML under a directory, run:

```
site.pamfletSupport("manual")
```

The above will place pamflet generated HTML under the `manual/` directory in the generated site.

### Nanoc Site Generation
The site plugin has direct support for building [nanoc][nanoc] projects. This assumes you have a nanoc project under the `src/nanoc` directory. To enable nanoc site generation, simply simply add the following to your `build.sbt` file:

```
site.nanocSupport()
```

### Asciidoctor Site Generation
The site plugin has direct support for building [Asciidoctor][asciidoctor] projects locally. This assumes you have a asciidoctor project under the `src/asciidoctor` directory. To enable asciidoctor site generation, simply add the following to your `build.sbt` file:

```
site.asciidoctorSupport()
```

## Scaladoc APIS
To include Scaladoc with your site, add the following line to your `build.sbt`:

```
site.includeScaladoc()
```

This will default to putting the scaladoc under the `latest/api` directory on the website. You can configure this by passing a parameter to the `includeScaladoc` method:

```
site.includeScaladoc("alternative/directory")
```

## Previewing the Site
To preview your generated site, run `previewSite` to open a web server. Direct your web browser to [http://localhost:4000/](http://localhost:4000/) to view your site.

## Packaging and Publishing
To create a zip package of the site run `package-site`.

To also publish this zip file when running `publish`, add the following to your `build.sbt`:

```
site.publishSite
```

## License

`sbt-site` is released under a "BSD 3-Clause" license. See [LICENSE](LICENSE) for specifics and copyright declaration.

[old]: https://github.com/sbt/sbt-site/tree/0.7.2
[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
[jekyll]: http://jekyllrb.com
[pamflet]: http://pamflet.databinder.net
[nanoc]: http://nanoc.ws/
[asciidoctor]: http://asciidoctor.org
[sphinx]: http://sphinx-doc.org
