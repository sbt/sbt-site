# Pamflet

The sbt-site plugin has direct support for building [Pamflet] projects. To enable Pamflet site generation, simply enable the associated plugin in your `build.sbt` file:

## The Pamflet integration is not (yet) included in this version of sbt-site.

@@ snip[enablePlugin](/pamflet/src/sbt-test/pamflet/can-use-pamflet/build.sbt) { #enablePlugin }

This assumes you have a Pamflet project under the `src/pamflet` directory. To change this, set the `sourceDirectory` key in the `Pamflet` scope:

```sbt
Pamflet / sourceDirectory := sourceDirectory.value / "papyrus"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `Pamflet` scope:

@@ snip[siteSubdirName](/pamflet/src/sbt-test/pamflet/can-use-pamflet/build.sbt) { #siteSubdirName }

[Pamflet]: http://www.foundweekends.org/pamflet/
