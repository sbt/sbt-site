# SBT Site plugin #

This is an SBT plugin that can generate project websites.   It is designed to work hand-in-hand with publishing plugins like [xsbt-ghpages-plugin](http://github.com/jsuereth/xsbt-ghpages-plugin).



## Usage ##

Add this to your `project/plugins.sbt`:

    resolvers += Resolver.url("sbt-plugin-releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

    resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

    addSbtPlugin("com.jsuereth" % "sbt-site-plugin" % "0.5.0")


And then in your `build.sbt` file:


    site.settings


Then, run `make-site` to generate your project's webpage in the `target/site` directory.   Again, see the [xsbt-ghpages-plugin](http://github.com/jsuereth/xsbt-ghpages-plugin) for publishing information to gh-pages.   We expect other publishing mechanisms to be supported in the future.

## Adding content to your site ##

The Site plugin has specific "support" settings for the various supported ways of generating a site.   By default, all files under `src/site` are included in the site.  You can add new generated content in one of the following ways:


### Direct ###

Simply add to the site mappings.  In your `build.sbt` file:


    site.siteMappings <++= Seq(file1 -> "location.html", file2 -> "image.png")


## Scaladoc APIS ###

Add the following line to your `build.sbt`:


    site.includeScaladoc()


This will default to putting the scaladoc under the `latest/api` directory on the website.  You can configure this by passing a parameter to the includeScaladoc method:


    site.includeScaladoc("alternative/directory")


### Jekyll site generation ###

The site plugin has direct support for running jekyll locally.  This is suprisingly useful for suporting custom jekyll plugins that are not allowed when publishing to github, or hosting a jekyll site on your own server.  To add jekyll support, add the following to your `build.sbt`:


    site.jekyllSupport()


This assumes you have a jekyll project in the `src/jekyll` directory.

*TODO - Link to documentation site on usage and configuration.*

One common issue with Jekyll is ensuring that everyone uses the same version for generating a website.  There is special support for ensuring the version of gems.  To do so, add the following to your `build.sbt` file:


    com.jsuereth.sbtsite.JekyllSupport.RequiredGems := Map(
      "jekyll" -> "0.11.2",
      "liquid" -> "2.3.0"
    )


### Sphinx site generation ###

The site plugin has direct support for building Sphinx projects locally.  This assumes you have a sphinx project under the `src/sphinx` directory.   To enable sphinx site generation, simply add the following to your `build.sbt` file:


    site.sphinxSupport()


### Pamflet generation ###

The site plugin has direct support for building [Pamflet](pamflet.databinder.net) projects.   This assumes you have a pamflet project under the `src/pamflet` directory.   To enable pamflet site generation, simplay add the following to your `build.sbt` file:


    site.pamfletSupport()

To place pamflet HTML under a directory, run:

    
    site.pamfletSupport("manual")


The above will place pamflet generated HTML under the `manual/` directory in the generated site.

### Changing the source directory ###

To change the source directory for static site files, use the `siteSourceDirectory` alias:


    siteSourceDirectory <<= target / "generated-stuff"

*TODO - Link to documentation site on usage and configuration.*

