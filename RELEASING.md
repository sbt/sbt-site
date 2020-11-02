## Releasing sbt-site

Releasing requires access to sbt's Bintray and GitHub pages.

1. use the GitHub release draft to create a new release https://github.com/sbt/sbt-site/releases
1. pull the tag to your local repository and **make sure it is clean**
1. publish the new version to https://bintray.com/sbt/sbt-plugin-releases/sbt-site with `sbt publish`
1. use the Bintray UI and publish the uploaded version
1. update the documentation with `sbt ghpagesPushSite`
