# Migration Guide

## Migrating from version 1.4.x to 1.5.x

TBD

## Migrating from version 1.3.x to 1.4.x

Only sbt >1.1.6 is supported.

### ParadoxSitePlugin

`ParadoxPlugin` is enabled whenever `ParadoxSitePlugin` is enabled.

Paradox integration now uses the same configuration scopes as the `sbt-paradox` plugin.
Therefore when upgrading remove the `Paradox` configuration scope from all of the `sbt-paradox` settings.
Note that sbt-site setting `siteSubdirName` is still configured under the `Paradox` configuration scope.

The default Paradox source directory has been changed to match the one defined in the `sbt-paradox`, which is `src/main/paradox`.
To keep the previous default, add the following to the build: `paradox / sourceDirectory := sourceDirectory.value`

[Preprocess]: preprocess.md#substitution
[Jekyll]: generators/jekyll.md
[Sphinx]: generators/sphinx.md
[Pamflet]: generators/pamflet.md
[Nanoc]: generators/nanoc.md
[Asciidoctor]: generators/asciidoctor.md
[Scaladoc]: api-documentation.md#scaladoc
