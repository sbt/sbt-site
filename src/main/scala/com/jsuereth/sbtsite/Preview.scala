package com.jsuereth.sbtsite

import sbt.{Path => _, _}
import Keys._
import unfiltered.request._
import unfiltered.response._
import unfiltered.util.{IO => _, _}
import java.io.OutputStream
import SiteKeys._

object Preview {

  //@TODO Add configuraiton to make server just local
  val settings: Seq[Setting[_]] = Seq(
    previewSite <<= (makeSite, previewFixedPort, previewLaunchBrowser) map { (file, portOption, browser) =>
      val port = portOption.getOrElse(Port.any)
      val server = createServer(file, port).start()
      println("Preview server started on port %d. Press any key to exit.".format(port))
      @annotation.tailrec def waitForKey() {
        try { Thread.sleep(500) } catch { case _: InterruptedException => () }
        if(System.in.available <= 0)
          waitForKey()
      }
      if(browser)
        Browser.open("http://localhost:%d/".format(port))
      waitForKey()
      server.stop();
      server.destroy();
    },
    previewFixedPort := Some(4000),
    previewLaunchBrowser := true
  )

  def createServer(siteTarget: File, port: Int) =
    unfiltered.jetty.Http(port).resources(new URL(siteTarget.toURI.toURL, "."))

}
