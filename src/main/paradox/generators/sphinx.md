# Sphinx

The sbt-site plugin has direct support for building [Sphinx] projects.

To enable the "sbt site Sphinx" plugin in your sbt project, add the following to your `project/plugins.sbt` file:

@@@ vars
```sbt
addSbtPlugin("com.github.sbt" % "sbt-site-sphinx" % "$project.version$")
```
@@@

And enable the `SphinxPlugin` plugin in your `build.sbt` file:

@@ snip[enablePlugin](/sphinx/src/sbt-test/sphinx/can-use-sphinx/build.sbt) { #enablePlugin }

This assumes you have a Sphinx project under the `src/sphinx` directory. To change this, set the `sourceDirectory` key in the `Sphinx` scope:

```sbt
Sphinx / sourceDirectory := sourceDirectory.value / "androsphinx"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Sphinx` scope:

@@ snip[siteSubdirName](/sphinx/src/sbt-test/sphinx/can-use-sphinx/build.sbt) { #siteSubdirName }

[Sphinx]: https://www.sphinx-doc.org
