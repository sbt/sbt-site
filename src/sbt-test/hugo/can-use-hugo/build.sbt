name := "test"

//#enablePlugin
enablePlugins(HugoPlugin)
//#enablePlugin

//#baseURL
baseURL in Hugo := uri("https://yourdomain.com")
//#baseURL

siteSubdirName in Hugo := "thisIsHugo"

TaskKey[Unit]("checkContent") := {
  val dest = (target in makeSite).value / (siteSubdirName in Hugo).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("Knobs")), s"Did not find expected content in:\n${content.mkString("\n")}")
}
