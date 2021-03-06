package com.typesafe.sbt.site

import sbt._
import Keys._
import unfiltered.util._

object SitePreviewPlugin extends AutoPlugin {
  override def requires = SitePlugin

  override def trigger = allRequirements

  object autoImport {
    val previewSite = TaskKey[Unit]("previewSite", "Launches a jetty server that serves your generated site from the target directory")
    val previewAuto = TaskKey[Unit]("previewAuto", "Launches an automatic jetty server that serves your generated site from the target directory")
    val previewFixedPort = SettingKey[Option[Int]]("previewFixedPort") in previewSite
    val previewLaunchBrowser = SettingKey[Boolean]("previewLaunchBrowser") in previewSite
    val previewPath = SettingKey[String]("previewPath", "path to open on `previewSite` and `previewAuto`") in previewSite
  }

  import SitePlugin.autoImport._
  import autoImport._

  override val projectSettings: Seq[Setting[_]] = Seq(
    previewSite := {
      val file = makeSite.value
      val portOption = previewFixedPort.value
      val browser = previewLaunchBrowser.value
      val path = previewPath.value

      val port = portOption.getOrElse(Port.any)
      val server = createServer(file, port).start()
      val sLog = streams.value.log
      sLog.info(s"SitePreviewPlugin server started on port $port. Press any key to exit.")
      if (browser)
        Browser.open("http://localhost:%d/%s".format(port, path))
      try System.in.read
      finally {
        server.stop()
        server.destroy()
      }
    },
    previewAuto := {
      val port = previewFixedPort.value.getOrElse(Port.any)
      val browser = previewLaunchBrowser.value
      val path = previewPath.value

      Preview(port, (target in previewAuto).value, makeSite, Compat.genSources, state.value).run { server =>
        if (browser)
          Browser.open(server.portBindings.head.url + "/" + path)
      }
    },
    previewFixedPort := Some(4000),
    previewLaunchBrowser := true,
    previewPath := "",
    target in previewAuto := siteDirectory.value
  )

  def createServer(siteTarget: File, port: Int) =
    unfiltered.jetty.Server.local(port) resources new URL(siteTarget.toURI.toURL, ".")

}
