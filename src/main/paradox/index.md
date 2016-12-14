# sbt-site

This sbt-site plugin generates project websites from static content as well as @ref:[third-party generators](generators/index.md) and can optionally include generated Scaladoc. It is designed to work hand-in-hand with publishing plugins like [sbt-ghpages].

Before upgrading please consult the @github:[release notes](/notes/). Instructions for upgrading from a version before 1.x.x can be found in the @github[migration guide].

@@@ note

* As of `sbt-site` version 1.x.x, sbt version >= 0.13.5 is required.
* For earlier 0.13.x releases, use [version 0.8.2][0.8.2].
* For sbt 0.12, use [version 0.7.2][0.7.2].

@@@

@@toc { depth=3 }

@@@ index

 - [getting-started](getting-started.md)
 - [preprocess](preprocess.md)
 - [generators](generators/index.md)
 - [advanced-usage](advanced-usage.md)

@@@

[0.7.2]: https://github.com/sbt/sbt-site/tree/v0.7.2
[0.8.2]: https://github.com/sbt/sbt-site/tree/v0.8.2
[migration guide]: /notes/migrate-0.8.2-to-1.0.md
[sbt-ghpages]: http://github.com/sbt/sbt-ghpages
