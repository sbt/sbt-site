# API documentation

## Scaladoc

To include Scaladoc with your site, add the following line to your `build.sbt`:

@@ snip[enablePlugin](../../sbt-test/scaladoc/can-add-scaladoc/build.sbt) { #enablePlugin }

This will default to putting the Scaladoc under the `latest/api` directory on the website. You can change this with the `siteSubdirName` key in the `SiteScaladoc` scope:

@@ snip[siteSubdirName](../../sbt-test/scaladoc/can-add-scaladoc/build.sbt) { #siteSubdirName }
