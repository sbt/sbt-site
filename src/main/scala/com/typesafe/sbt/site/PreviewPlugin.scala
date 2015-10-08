package com.typesafe.sbt
package site

import sbt._
import unfiltered.util._

object PreviewPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = allRequirements

  object autoImport {
    val previewSite = TaskKey[Unit]("preview-site", "Launches a jetty server that serves your generated site from the target directory")
    val previewFixedPort = SettingKey[Option[Int]]("previewFixedPort") in previewSite
    val previewLaunchBrowser = SettingKey[Boolean]("previewLaunchBrowser") in previewSite
  }
  import SitePlugin.autoImport._
  import autoImport._


  //@TODO Add configuration to make server just local
  override val projectSettings: Seq[Setting[_]] = Seq(
    previewSite <<= (makeSite, previewFixedPort, previewLaunchBrowser) map { (file, portOption, browser) =>
      val port = portOption getOrElse Port.any
      val server = createServer(file, port) start()
      println("PreviewPlugin server started on port %d. Press any key to exit." format port)
      // TODO: use something from sbt-web?
      @annotation.tailrec def waitForKey() {
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
    previewFixedPort := Some(4000),
    previewLaunchBrowser := true
  )

  def createServer(siteTarget: File, port: Int) =
    unfiltered.jetty.Http(port) resources new URL(siteTarget.toURI.toURL, ".")

}
