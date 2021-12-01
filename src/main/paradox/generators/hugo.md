# Hugo

The sbt-site plugin has support for building [Hugo] projects. To enable Hugo site generation, simply enable the associated plugin in your `build.sbt` file:

@@ snip[enablePlugin](/src/sbt-test/hugo/can-use-hugo/build.sbt) { #enablePlugin }

The `hugo` binary must be installed on your `$PATH` in order to be accessible to sbt-site. In addition, this plugin assumes you have a Hugo project under the `src/hugo` directory. To change this, set the `sourceDirectory` key in the `Hugo` scope:

```sbt
Hugo / sourceDirectory := sourceDirectory.value / "doc"
```

You may also change the [base-url](https://gohugo.io/overview/configuration/) that gets passed to the `hugo` command by adjusting the following setting:

@@ snip[baseURL](/src/sbt-test/hugo/can-use-hugo/build.sbt) { #baseURL }

To export environment variables when forking the `hugo` process, for example to render with Hugo's [getenv function](https://gohugo.io/functions/getenv/), use:

@@ snip[extraEnv](/src/sbt-test/hugo/can-use-hugo/build.sbt) { #extraEnv }

[Hugo]: https://gohugo.io/
