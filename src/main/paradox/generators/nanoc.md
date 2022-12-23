# Nanoc

The sbt-site plugin has direct support for building [Nanoc] projects. To enable Nanoc site generation, simply enable the associated plugin in your `build.sbt` file:

## The nanoc integration is not (yet) included in this version of sbt-site.

@@ snip[enablePlugin](/nanoc/src/sbt-test/nanoc/can-use-nanoc/build.sbt) { #enablePlugin }

This assumes you have a Nanoc project under the `src/nanoc` directory. To change this, set the `sourceDirectory` key in the `Nanoc` scope:

```sbt
Nanoc / sourceDirectory := sourceDirectory.value / "conan"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Nanoc` scope:

@@ snip[siteSubdirName](/nanoc/src/sbt-test/nanoc/can-use-nanoc/build.sbt) { #siteSubdirName }

[Nanoc]: https://nanoc.ws/
