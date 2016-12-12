# Advanced Usage

Here are listed examples of a more advanced usage.

## Running a Site Generator on Multiple Source Directories

If you need to run a generator on more than one source directory, bypassing the `AutoPlugin` system and defining one or more sbt `Configuration`s is necessary. For example, suppose you have two Paradox source directories and want them each generated as a subdirectory under `target/site`. A `build.sbt` might look something like this:

@@ snip[advanced-usage](../../sbt-test/site/can-run-generator-twice/build.sbt) { #advanced-usage }

The other generators follow a similar pattern, e.g. `JekyllPlugin.jekyllSettings(config("foo"))`.

## Preprocessing Markdown files with tut

The [tut] sbt plugin allows you to write documentation that is typechecked and run as part of your build. The following example shows how to use it to preprocess a collection of markdown files before running a site generator. In the example, we will use Paradox but this applies to any site generator which understands markdown, for example Jekyll as used by [sbt-microsites].

@@ snip[tut](../../sbt-test/site/plays-nice-with-tut/build.sbt) { #tut }

[tut]: https://github.com/tpolecat/tut
[sbt-microsites]: https://47deg.github.io/sbt-microsites/
