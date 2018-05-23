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
  }
  import SitePlugin.autoImport._
  import autoImport._

  override val projectSettings: Seq[Setting[_]] = Seq(
    previewSite := {
      val file = makeSite.value
      val portOption = previewFixedPort.value
      val browser = previewLaunchBrowser.value

      val port = portOption getOrElse Port.any
      val server = createServer(file, port) start()
      val sLog = streams.value.log
      sLog.info("SitePreviewPlugin server started on port %d. Press any key to exit." format port)
      // TODO: use something from sbt-web?
      @annotation.tailrec def waitForKey(): Unit = {
        try { Thread sleep 500 } catch { case _: InterruptedException => () }
        if(System.in.available <= 0)
          waitForKey()
      }
      if(browser)
        Browser open ("http://localhost:%d/" format port)
      waitForKey()
      server stop()
      server destroy()
    },
    previewAuto := {
      val port = previewFixedPort.value getOrElse Port.any
      val browser = previewLaunchBrowser.value

      Preview(port, (target in previewAuto).value, makeSite, Compat.genSources, state.value) run { server =>
        if (browser)
          Browser open(server.portBindings.head.url)
      }
    },
    previewFixedPort := Some(4000),
    previewLaunchBrowser := true,
    target in previewAuto := siteDirectory.value
  )

  def createServer(siteTarget: File, port: Int) =
    unfiltered.jetty.Server.local(port) resources new URL(siteTarget.toURI.toURL, ".")

}
