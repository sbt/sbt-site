# Paradox

The sbt-site plugin has direct support for building [Paradox] projects.

To enable Paradox site generation, enable the `ParadoxSitePlugin` plugin in your `build.sbt` file:

@@ snip[enablePlugin](/src/sbt-test/paradox/can-use-paradox/build.sbt) { #enablePlugin }

This assumes you have a Paradox project under the `src/main/paradox` directory.
To change this, set the `sourceDirectory` key in the `paradox` task scope:

```sbt
paradox / sourceDirectory := sourceDirectory.value / "doc"
```

If you are configuring Paradox from scratch remember to also configure a theme:

@@ snip[paradoxTheme](/src/sbt-test/paradox/can-use-paradox/build.sbt) { #paradoxTheme }

Note that all of the Paradox settings mentioned in the [Paradox documentation] are reused by sbt-site.
For example to configure an `@extref` link prefix use:

@@ snip[paradoxProperties](/src/sbt-test/paradox/can-use-paradox/build.sbt) { #paradoxProperties }

[Paradox]: https://github.com/lightbend/paradox
[Paradox documentation]: https://developer.lightbend.com/docs/paradox/latest/
