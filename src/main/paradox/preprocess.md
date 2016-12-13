# Variable Substitution

The site plugin supports basic variable substitution when copying files from `src/site-preprocess`. To enable, add this to your `build.sbt` file:

@@ snip[enablePlugin](../../sbt-test/preprocess/does-transform-variables/build.sbt) { #enablePlugin }

Variables are delimited by surrounding the name with `@` symbols (e.g. `@VERSION@`). Values are assigned to variables via the setting `preprocessVars: [Map[String, String]]`. For example:

@@ snip[preprocessVars](../../sbt-test/preprocess/does-transform-variables/build.sbt) { #preprocessVars }

Note that the plugin will generate an error if a variable is found in the source file with no matching value in `preprocessVars`.

The setting `preprocessIncludeFilter` is used to define the filename extensions that should be processed when `makeSite` is run.

@@ snip[preprocessIncludeFilter](../../sbt-test/preprocess/does-transform-variables/build.sbt) { #preprocessIncludeFilter }

The default filter is:

@@ snip[preprocessIncludeFilter](../scala/com/typesafe/sbt/site/preprocess/PreprocessPlugin.scala) { #preprocessIncludeFilter }
