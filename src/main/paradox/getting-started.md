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

## Packaging

To create a zip package of the site run `package-site`.

To also include this zip file as an artifact when running `publish`, add the following to your `build.sbt`:

```sbt
publishSite
```

## Publishing

To publish a generated site to [GitHub Pages] you can use the [sbt-ghpages]
plugin. We expect other publishing mechanisms to be supported in the future.

### Publishing to GitHub Pages using sbt-ghpages

Set up the plugin by adding the following to `project/plugins.sbt` (check the
[sbt-ghpages project][sbt-ghpages] for the most recent version).

@@ snip[sbt-ghpages](../../../project/plugins.sbt) { #sbt-ghpages }

Then configure your sbt build to use a special remote when running on Travis CI
and otherwise fall back to the normal Git remote configured via the `scmInfo`
setting.

@@ snip[ghpages-publish](../../../build.sbt) { #scm-info #ghpages-publish }

At this point you should be able to run `sbt ghpagesPushSite` to publish your
site.

@@@ note

Before running `sbt ghpagesPushSite` the first time you need to create the
`gh-pages` branch. One way to do it is to build your site and publish a first
version of it using the following commands:

```sh
$ sbt clean make-site                             # <1> Build the site
$ origin=$(git remote get-url origin)             # <2> Save the current remote for later
$ cd target/site
$ git init                                        # <3> Create site repo and add initial content
$ git add .
$ git commit -m "Initial import of GitHub Pages"
$ git push --force "$origin" master:gh-pages      # <4> Publish the repo's master branch as gh-pages
```

@@@

[apidoc]: api-documentation.md
[sbt file mappings]: http://www.scala-sbt.org/0.13/docs/Mapping-Files.html
[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
[GitHub Pages]: https://pages.github.com
