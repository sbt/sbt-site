# sbt-site #

This is an sbt plugin that can generate project websites.

It is designed to work hand-in-hand with publishing plugins like [sbt-ghpages].


## Usage ##

Add this to your `project/plugins.sbt`:

    addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.1")

*Note: this requires sbt 0.12 - for sbt 0.11 see the [0.4 version][old] of this plugin.*

And then in your `build.sbt` file:

    site.settings

Then, run `make-site` to generate your project's webpage in the `target/site` directory.

See the [sbt-ghpages] plugin for information about publishing to gh-pages. We expect other
publishing mechanisms to be supported in the future.


## Adding content to your site ##

The sbt-site plugin has specific "support" settings for the various supported ways
of generating a site. By default, all files under `src/site` are included in the site.
You can add new generated content in one of the following ways:


### Direct ###

Simply add to the site mappings. In your `build.sbt` file:

    site.siteMappings <++= Seq(file1 -> "location.html", file2 -> "image.png")


## Scaladoc APIS ###

Add the following line to your `build.sbt`:

    site.includeScaladoc()

This will default to putting the scaladoc under the `latest/api` directory on the
website. You can configure this by passing a parameter to the includeScaladoc method:

    site.includeScaladoc("alternative/directory")


### Jekyll site generation ###

The site plugin has direct support for running jekyll locally.  This is suprisingly
useful for suporting custom jekyll plugins that are not allowed when publishing to
github, or hosting a jekyll site on your own server. To add jekyll support, add the
following to your `build.sbt`:

    site.jekyllSupport()

This assumes you have a jekyll project in the `src/jekyll` directory.

*TODO - Link to documentation site on usage and configuration.*

One common issue with Jekyll is ensuring that everyone uses the same version for
generating a website.  There is special support for ensuring the version of gems.
To do so, add the following to your `build.sbt` file:

    com.typesafe.sbt.site.JekyllSupport.RequiredGems := Map(
      "jekyll" -> "0.11.2",
      "liquid" -> "2.3.0"
    )


### Sphinx site generation ###

The site plugin has direct support for building Sphinx projects locally. This assumes
you have a sphinx project under the `src/sphinx` directory. To enable sphinx site
generation, simply add the following to your `build.sbt` file:

    site.sphinxSupport()


### Pamflet generation ###

**Note:** Pamflet support was temporarily removed due to scala binary versioning issues.
Once we can load pamflet via reflection, or it has a 2.10 compatible release, it will
be re-enabled.


### Changing the source directory ###

To change the source directory for static site files, use the `siteSourceDirectory` alias:

    siteSourceDirectory <<= target / "generated-stuff"


### Previewing the site ###

To preview your generated site, run `preview-site` to open a web server. Direct your
web browser to `http://localhost:4000/` to view your site.

*TODO - Link to documentation site on usage and configuration.*


## Packaging and Publishing ##

To create a zip package of the site run `package-site`.

To also publish this zip file when running `publish`, add the following to your `build.sbt`:

    site.publishSite


[old]: https://github.com/sbt/sbt-site/tree/0.4.0
[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
[Pamflet]: http://pamflet.databinder.net
