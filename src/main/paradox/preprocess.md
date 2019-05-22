# Preprocessing

sbt-site supports simple variable and Regular Expression substitution via the `PreprocessPlugin` if for example you need to replace a version string in a source file or a generated HTML file. More advanced preprocessing, such as interpreting code snippets with [tut], is possible via additional configuration as shown below.

## Substitution

The `PreprocessPlugin` reads files from an input directory, substitute variables and writes them to an output directory. In addition to preprocessing static content it is also possible to use the plugin either before or after invoking a @ref:[site generator](generators/index.md). To enable, add this to your `build.sbt` file:

@@ snip[enablePlugin](/src/sbt-test/preprocess/does-transform-variables/build.sbt) { #enablePlugin }

By default files are read from `src/site-preprocess` but this is configurable by setting `sourceDirectory`:

@@ snip[sourceDirectory](/src/sbt-test/preprocess/does-transform-variables/build.sbt) { #sourceDirectory }

Variables are delimited by surrounding the name with `@` symbols (e.g. `@VERSION@`). Values are assigned to variables via the setting `preprocessVars: Map[String, String]`. For example:

@@ snip[preprocessVars](/src/sbt-test/preprocess/does-transform-variables/build.sbt) { #preprocessVars }

@@@ note
The plugin will generate an error if a variable is found in the source file with no matching value in `preprocessVars`.
@@@

More advanced substitution patterns can be used by providing Regular Expression rules via the setting `preprocessRules: Seq[(Regex, Match => String)]`.
For example Scaladoc used to (before 2.12.9 and 2.13.0) prepend ".scala" to source links of Java files.
The following preprocessing rule will find and fix such links:

@@ snip[preprocessRules](/src/sbt-test/preprocess/transform-scaladoc/build.sbt) { #preprocessRules }

The setting `preprocessIncludeFilter` is used to define the filename extensions that should be processed when `makeSite` is run.

@@ snip[preprocessIncludeFilter](/src/sbt-test/preprocess/does-transform-variables/build.sbt) { #preprocessIncludeFilter }

The default filter is:

@@ snip[preprocessIncludeFilter](/src/main/scala/com/typesafe/sbt/site/preprocess/PreprocessPlugin.scala) { #preprocessIncludeFilter }

## Preprocessing Markdown files with tut

The [tut] sbt plugin allows you to write documentation with fenced code blocks that is typechecked and run as part of your build. For example this markdown snippet:

@@ snip[tut](/src/sbt-test/site/plays-nice-with-tut/src/main/tut/index.md) { #tut }

Will show the result of running the code in the Scala REPL:

    Here is how you add numbers:

    ```scala
    scala> 1 + 1
    res0: Int = 2    
    ```

The following example shows how to use it to preprocess a collection of markdown files before running a site generator.

@@ snip[tut](/src/sbt-test/site/plays-nice-with-tut/build.sbt) { #tut }

The example uses the Paradox site generator but can be adapted to any site generator which understands markdown by configuring its `sourceDirectory` accordingly. For Jekyll, this would be:

```sbt
Jekyll / sourceDirectory := tutTargetDirectory.value
```

[tut]: https://github.com/tpolecat/tut
[sbt-microsites]: https://47deg.github.io/sbt-microsites/
