# Sphinx

The sbt-site plugin has direct support for building [Sphinx] projects. To enable Sphinx site generation, simply enable the associated plugin in your `build.sbt` file:

@@ snip[enablePlugin](/src/sbt-test/sphinx/can-use-sphinx/build.sbt) { #enablePlugin }

This assumes you have a Sphinx project under the `src/sphinx` directory. To change this, set the `sourceDirectory` key in the `Sphinx` scope:

```sbt
Sphinx / sourceDirectory := sourceDirectory.value / "androsphinx"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Sphinx` scope:

@@ snip[siteSubdirName](/src/sbt-test/sphinx/can-use-sphinx/build.sbt) { #siteSubdirName }

[Sphinx]: http://www.sphinx-doc.org
