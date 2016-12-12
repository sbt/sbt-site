# Jekyll

The `sbt-site` plugin has direct support for running [Jekyll]. This is useful for supporting custom Jekyll plugins that are not allowed when publishing to GitHub, or hosting a Jekyll site on your own server. To add Jekyll support, enable the associated plugin:

@@ snip[enablePlugin](../../../sbt-test/jekyll/can-use-jekyll/build.sbt) { #enablePlugin }

This assumes you have a Jekyll project in the `src/jekyll` directory. To change this, set the key `sourceDirectory` in the `Jekyll` scope:

```sbt
sourceDirectory in Jekyll := sourceDirectory.value / "hyde"
```

To redirect the output to a subdirectory of `target/site`, use the `siteSubdirName` key in `Jekyll` scope:

@@ snip[siteSubdirName](../../../sbt-test/jekyll/can-use-jekyll/build.sbt) { #siteSubdirName }

One common issue with Jekyll is ensuring that everyone uses the same version for generating a website. There is special support for ensuring the version of gems. To do so, add the following to your `build.sbt` file:

@@ snip[requiredGems](../../../sbt-test/jekyll/can-use-jekyll/build.sbt) { #requiredGems }

[Jekyll]: http://jekyllrb.com
