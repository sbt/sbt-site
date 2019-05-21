# Paradox

The sbt-site plugin has direct support for building [Paradox] projects.

To enable Paradox site generation, simply enable the associated plugin in your `build.sbt` file:

@@ snip[enablePlugin](../../../sbt-test/paradox/can-use-paradox/build.sbt) { #enablePlugin }

This assumes you have a Paradox project under the `src/paradox` directory. To change this, set the `sourceDirectory` key in the `Paradox` scope:

```sbt
sourceDirectory in Paradox := sourceDirectory.value / "doc"
```

If you are configuring Paradox from scratch remember to also configure a theme:

@@ snip[paradoxTheme](../../../sbt-test/paradox/can-use-paradox/build.sbt) { #paradoxTheme }

Note that Paradox settings such as `paradoxProperties` should be scoped to `Paradox` instead of `Compile` as used in the [Paradox documentation]. For example to configure an `@extref` link prefix use:

@@ snip[paradoxProperties](../../../sbt-test/paradox/can-use-paradox/build.sbt) { #paradoxProperties }

[Paradox]: https://github.com/lightbend/paradox
[Paradox documentation]: https://developer.lightbend.com/docs/paradox/latest/
