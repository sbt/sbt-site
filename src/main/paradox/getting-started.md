# Getting Started

`sbt-site` is deployed as an `AutoPlugin`. To enable, simply add the following to your `project/plugins.sbt` file:

@@@ vars
```sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "$project.version$")
```
@@@

When you run `makeSite`, your project's webpage is generated in the `target/site` directory. By default, all files under `src/site` are included in `target/site`. To use specific @ref:[third-party generators](generators/index.md) (e.g. Jekyll), additional sub-plugins will need to be enabled.

The `src/site` directory can be overridden via the `siteSourceDirectory` key:

```sbt
siteSourceDirectory := target.value / "generated-stuff"
```

## Mapping Content to Your Site

Additional files outside of `siteSourceDirectory` can be added individually via `mappings in makeSite`:

@@ snip[mappings](../../sbt-test/site/can-have-custom-mappings/build.sbt) { #mappings }

Or if such files should be added in a separate directory via `addMappingsToSiteDir`:

@@ snip[addMappingsToSiteDir](../../sbt-test/site/can-have-custom-mappings/build.sbt) { #addMappingsToSiteDir }

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

[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
[GitHub Pages]: https://pages.github.com
