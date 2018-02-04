# sbt-site

This sbt plugin generates project websites from static content as well as @ref:[third-party generators](generators/index.md) and can optionally include generated Scaladoc. It is designed to work hand-in-hand with publishing plugins like [sbt-ghpages].

Before upgrading please consult the @github:[release notes](/notes/). Instructions for upgrading from a version before 1.x.x can be found in the @ref:[migration guide](migration-guide.md#from-version-0.x.x-to-1.x.x).

@@@ note

* Version 1.3.0 is cross published to both sbt 0.13 and sbt 1.x, however, the
  [Laika](generators/laika.md) generator is currently only available for sbt 0.13.
* As of sbt-site version 1.x.x, sbt version 0.13.10+ or 1.0.0-RC2+ is required.
* For earlier 0.13.x releases, use [version 0.8.2][0.8.2].
* For sbt 0.12, use [version 0.7.2][0.7.2].

@@@

@@toc { depth=3 }

@@@ index

 - [getting-started](getting-started.md)
 - [preprocess](preprocess.md)
 - [api-documentation](api-documentation.md)
 - [generators](generators/index.md)
 - [publishing](publishing.md)
 - [advanced-usage](advanced-usage.md)
 - [migration-guide](migration-guide.md)

@@@

[0.7.2]: https://github.com/sbt/sbt-site/tree/v0.7.2
[0.8.2]: https://github.com/sbt/sbt-site/tree/v0.8.2
[sbt-ghpages]: https://github.com/sbt/sbt-ghpages
