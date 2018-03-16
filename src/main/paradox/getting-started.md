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

When you run `makeSite`, your project's webpage is generated in the `target/site` directory. By default, all files under `src/site` are included in `target/site`. If your site mainly contains static content but you want to replace for example a version string in some of the pages you can use @ref:[preprocessing](preprocess.md) to substitute variables.

In addition to static content, you can also generated content as part of the build process and add it to your site. sbt-site has support for adding @ref:[Scaladoc][apidoc] and provides several @ref:[site generators](generators/index.md) such as Jekyll and Sphinx which can be used for managing your site's content.

If you already have a way to generate a site and all you want is to use sbt-site to package and maybe include @ref:[API documentation][apidoc], you can configure `siteSourceDirectory` to point to the directory containing the generated site files instead of `src/site`:

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

To create a zip package of the site run `packageSite`.

To also include this zip file as an artifact when running `publish`, add the following to your `build.sbt`:

@@ snip[publishSite](../../sbt-test/site/can-package-and-publish-zip-file/build.sbt) { #publishSite }

Once you have generated and packaged your site the next step is to publish it. The @ref:[publishing](publishing.md) section discusses several mechanisms, such as [sbt-ghpages].

[apidoc]: api-documentation.md
[sbt file mappings]: https://www.scala-sbt.org/0.13/docs/Mapping-Files.html
[sbt-ghpages]: https://github.com/sbt/sbt-ghpages
