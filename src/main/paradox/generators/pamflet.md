# Pamflet

The `sbt-site` plugin has direct support for building [Pamflet] projects. To enable Pamflet site generation, simply enable the associated plugin in your `build.sbt` file:

@@ snip[enablePlugin](../../../sbt-test/pamflet/can-use-pamflet/build.sbt) { #enablePlugin }

This assumes you have a Pamflet project under the `src/pamflet` directory. To change this, set the `sourceDirectory` key in the `Pamflet` scope:

```sbt
sourceDirectory in Pamflet := sourceDirectory.value / "papyrus"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Pamflet` scope:

@@ snip[siteSubdirName](../../../sbt-test/pamflet/can-use-pamflet/build.sbt) { #siteSubdirName }

[Pamflet]: http://pamflet.databinder.net
