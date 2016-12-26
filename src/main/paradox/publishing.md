# Publishing

The following sections show you how to configure publishing of your site. To
publish a generated site to [GitHub Pages] you can use the [sbt-ghpages] plugin.
For sites hosted on S3 one option is to use [sbt-s3]. We expect other publishing
mechanisms to be supported in the future.

## Publishing to GitHub Pages using sbt-ghpages

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

[sbt-s3]: https://github.com/sbt/sbt-s3
[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
[GitHub Pages]: https://pages.github.com
