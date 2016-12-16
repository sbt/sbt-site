# Getting Started

This page will help you get started with sbt-site and teach you the basic concepts.

## Setup

To enable the plugin in your sbt project, add the following to your `project/plugins.sbt` file:

@@@ vars
```sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "$project.version$")
```
@@@

## Adding Content to Your Site

When you run `makeSite`, your project's webpage is generated in the `target/site` directory. By default, all files under `src/site` are included in `target/site`. In addition to static content, @ref:[site generators](generators/index.md) supporting tools such as Jekyll and Sphinx can be used for managing your site's content.

If you already have a way to generate a site and all you want is to use sbt-site to package and maybe include API documentation, you can configure `siteSourceDirectory` to point to the directory containing the generated site files instead of `src/site`:

```sbt
siteSourceDirectory := target.value / "generated-stuff"
```

Additional files outside of `siteSourceDirectory` can be added via [sbt file mappings]:

@@ snip[mappings](../../sbt-test/site/can-have-custom-mappings/build.sbt) { #mappings }

If you want to add files from an sbt task to a site sub-directory use the provided `addMappingsToSiteDir`:

@@ snip[addMappingsToSiteDir](../../sbt-test/site/can-have-custom-mappings/build.sbt) { #addMappingsToSiteDir }

@@@ note

`addMappingsToSiteDir` requires that the site sub-directory name is passed via a
setting's key. In most cases this can be achieved by scoping sbt-site's
`siteSubdirName` setting's key to the task providing the mappings as shown
above.

@@@

## Scaladoc APIs

To include Scaladoc with your site, add the following line to your `build.sbt`:

@@ snip[enablePlugin](../../sbt-test/site/can-add-scaladoc/build.sbt) { #enablePlugin }

This will default to putting the Scaladoc under the `latest/api` directory on the website. You can change this with the `siteSubdirName` key in the `SiteScaladoc` scope:

@@ snip[siteSubdirName](../../sbt-test/site/can-add-scaladoc/build.sbt) { #siteSubdirName }

## Previewing the Site

To preview your generated site, you can run `previewSite` which launches a static web server, or `previewAuto` which launches a dynamic server updating its content at each modification in your source files. Both launch the server on port 4000 and attempts to connect your browser to [http://localhost:4000/](http://localhost:4000/). To change the server port, use the key `previewFixedPort`:

```sbt
previewFixedPort := Some(9999)
```

To disable browser auto-open, use the key `previewLaunchBrowser`:

```sbt
previewLaunchBrowser := false
```

## Packaging and Publishing

To create a zip package of the site run `package-site`.

To also include this zip file as an artifact when running `publish`, add the following to your `build.sbt`:

```sbt
publishSite
```

To publish a generated site to [GitHub Pages] use the [sbt-ghpages] plugin.
We expect other publishing mechanisms to be supported in the future.

[sbt file mappings]: http://www.scala-sbt.org/0.13/docs/Mapping-Files.html
[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
[GitHub Pages]: https://pages.github.com
