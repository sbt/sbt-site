name := "test"

//#enablePlugin
enablePlugins(JekyllPlugin)
//#enablePlugin

//#siteSubdirName
// Puts output in `target/site/notJekyllButHyde`
siteSubdirName in Jekyll := "notJekyllButHyde"
//#siteSubdirName

/* FIXME: This currently fails
[error] /private/var/folders/tc/dfx5mzxn1x77054_d_551wd40000gn/T/sbt_75e23137/can-use-jekyll/build.sbt:13: error: reference to requiredGems is ambiguous;
[error] it is imported twice in the same scope by
[error] import _root_.com.typesafe.sbt.site.nanoc.NanocPlugin.autoImport._
[error] and import _root_.com.typesafe.sbt.site.jekyll.JekyllPlugin.autoImport._
[error] requiredGems := Map(
[error] ^

//#requiredGems
requiredGems := Map(
  "jekyll" -> "2.5.3",
  "liquid" -> "2.3.0"
)
//#requiredGems
*/

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Jekyll).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("sbt")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
