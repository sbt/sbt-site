# GitBook

The sbt-site plugin has direct support for building [GitBook] projects. 

To enable the "sbt site GitBook" plugin in your sbt project, add the following to your `project/plugins.sbt` file:

@@@ vars
```sbt
addSbtPlugin("com.github.sbt" % "sbt-site-gitbook" % "$project.version$")
```
@@@

And enable the associated plugin in your `build.sbt` file:

@@ snip[enablePlugin](/gitbook/src/sbt-test/gitbook/ignore-dot-files/build.sbt) { #enablePlugin }

This assumes you have a GitBook project under the `src/gitbook` directory. To change this, set the `sourceDirectory` key in the `GitBook` scope:

```sbt
GitBook / sourceDirectory := sourceDirectory.value / "doc"
```

Similarly, the output can be redirected to a subdirectory of `target/site` via the `siteSubdirName` key in `GitBook` scope:

@@ snip[siteSubdirName](/gitbook/src/sbt-test/gitbook/ignore-dot-files/build.sbt) { #siteSubdirName }

The plugin can also be configured to manage all GitBook setup and installation by configuring a dedicated directory in which GitBook's npm packages can be installed.

@@ snip[gitbookInstallDir](/gitbook/src/sbt-test/gitbook/can-manage-installation/build.sbt) { #gitbookInstallDir }

[GitBook]: https://www.gitbook.com
