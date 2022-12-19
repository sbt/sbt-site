name := "test"

//#enablePlugin
enablePlugins(HugoPlugin)
//#enablePlugin

//#baseURL
Hugo / baseURL := uri("https://yourdomain.com")
//#baseURL

Hugo / siteSubdirName := "thisIsHugo"

//#extraEnv
Hugo / extraEnv := Map("MY_ENV" -> "Is set!", "MY_OTHER_ENV" -> "Is also set.")
//#extraEnv

TaskKey[Unit]("checkContent") := {
  val dest = (makeSite / target).value / (Hugo / siteSubdirName).value
  val index = dest / "index.html"
  assert(index.exists, s"${index.getAbsolutePath} did not exist")
  val content = IO.readLines(index)
  assert(content.exists(_.contains("Knobs")), s"Did not find expected content in:\n${content.mkString("\n")}")
  val myEnvText = "MY_ENV=&quot;Is set!&quot;"
  assert(content.exists(_.contains(myEnvText)), s"MY_ENV was not rendered in index.html via shortcode. Expected to find: $myEnvText")
  val myOtherEnvText = "MY_OTHER_ENV=&quot;Is also set.&quot;"
  assert(content.exists(_.contains(myOtherEnvText)), s"MY_OTHER_ENV was not rendered in index.html via shortcode. Expected to find: $myOtherEnvText")
}
