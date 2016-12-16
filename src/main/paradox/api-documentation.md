# API documentation

## Scaladoc

To include Scaladoc with your site, add the following line to your `build.sbt`:

@@ snip[enablePlugin](../../sbt-test/scaladoc/can-add-scaladoc/build.sbt) { #enablePlugin }

This will default to putting the Scaladoc under the `latest/api` directory on the website. You can change this with the `siteSubdirName` key in the `SiteScaladoc` scope:

@@ snip[siteSubdirName](../../sbt-test/scaladoc/can-add-scaladoc/build.sbt) { #siteSubdirName }

## Aggregating API documentation with sbt-unidoc

[sbt-unidoc] allows to aggregate Scaladoc or Javadoc from sub-projects into a unified view. The following example shows how you can add aggregated Scaladoc to your site:

@@ snip[unidoc-site](../../sbt-test/scaladoc/can-aggregate/build.sbt) { #subprojects #unidoc-site }

[sbt-unidoc]: https://github.com/sbt/sbt-unidoc

## Scaladoc from multiple projects

In case you want to include the Scaladoc separate for each sub-product there are several options. If
you only have a few sub-projects the simplest solution is to manually include
the scaladoc for each project:

@@ snip[scaladoc-site](../../sbt-test/scaladoc/can-aggregate/build.sbt) { #subprojects #scaladoc-site }

For projects with many such sub-projects, a more maintainable approach is to configure it in a more programatic way:

@@ snip[scaladoc-site-alternative](../../sbt-test/scaladoc/can-aggregate/build.sbt) { #subprojects #scaladoc-site-alternative }
