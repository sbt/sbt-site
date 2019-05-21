# Laika

The sbt-site plugin has support for building [Laika] projects.
To enable Laika site generation, simply enable the associated plugin in your `build.sbt` file:

@@ snip[enablePlugin](../../../sbt-test/laika_0.13/minimal/build.sbt) { #enablePlugin }

This plugin assumes you have a Laika project under the `src/laika` directory. To change this, set the `sourceDirectory` key in the `LaikaSite` scope:

```sbt
sourceDirectory in LaikaSite := sourceDirectory.value / "doc"
```

This plugin use [Laika](https://github.com/planet42/Laika) sbt plugin internally and redefine default values for several Laika sbt keys which related to `sourceDirectory` in order to both work in harmony.
Default setting of Laika sbt will be included automatically once you enable this plugin, you don't need to include them manually and
doing so may result in conflict.

From other hand you can customize other aspects of Laika's behavior through basic
[Laika sbt plugin](https://planet42.github.io/Laika/using-laika/sbt.html) keys and hooks.
For example in order to add custom block directives you can include such code to your build.sbt:

sbt 0.13
:   @@ snip[laikaSbtPluginCustomization](../../../sbt-test/laika_0.13/blog-post/build.sbt) { #laikaSbtPluginCustomization }

sbt 1.x
:   @@ snip[laikaSbtPluginCustomization](../../../sbt-test/laika/blog-post/build.sbt) { #laikaSbtPluginCustomization }

[Laika]: https://github.com/planet42/Laika
