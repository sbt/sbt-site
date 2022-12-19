# Releasing sbt-site

- [Releases](https://github.com/sbt/sbt-site/releases)
- [Documentation](https://www.scala-sbt.org/sbt-site/)

## Publish a new release
- [ ] Update the [draft release](https://github.com/sbt/sbt-site/releases) with the next tag version `vX.Y.Z`, title and release description. Use the `Publish release` button, which will create the tag.
- [ ] Check that GitHub Actions release build executes successfully (GitHub Actions will start a [CI build](https://github.com/sbt/sbt-site/actions) for the new tag and publish artifacts to Maven central via Sonatype and the documentation to GitHub pages)
