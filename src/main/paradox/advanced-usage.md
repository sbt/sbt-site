# Advanced Usage

Here are listed examples of a more advanced usage.

## Running a Site Generator on Multiple Source Directories

If you need to run a generator on more than one source directory, bypassing the `AutoPlugin` system and defining one or more sbt `Configuration`s is necessary. For example, suppose you have two Paradox source directories and want them each generated as a subdirectory under `target/site`. A `build.sbt` might look something like this:

@@ snip[advanced-usage](/src/sbt-test/site/can-run-generator-twice/build.sbt) { #advanced-usage }

The other generators follow a similar pattern, e.g. `JekyllPlugin.jekyllSettings(config("foo"))`.
