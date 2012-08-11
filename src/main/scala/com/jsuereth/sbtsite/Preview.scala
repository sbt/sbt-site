package com.jsuereth.sbtsite

import sbt.{Path => _, _}
import Keys._
import unfiltered.request._
import unfiltered.response._
import unfiltered.util.RunnableServer
import java.io.OutputStream

object Preview {

  val previewSite = TaskKey[Unit]("preview-site", "Launches a jetty server that serves your generated site from the target directory")

  //@TODO Add configuration for port
  //@TODO Add configuraiton for whether server is just local
  lazy val settings: Seq[Setting[_]] = Seq(
    previewSite <<= (SiteKeys.makeSite) map { file =>
      val server = createServer(file).start()
      //@TODO Lanuch a browser?
      println("Preview server started on port 4000. Press any key to exit.")
      @annotation.tailrec def waitForKey() {
        try { Thread.sleep(500) } catch { case _: InterruptedException => () }
        if(System.in.available <= 0)
          waitForKey()
      }
      waitForKey()
      server.stop();
      server.destroy();
    }
  )

  def createServer(siteTarget: File) =
    unfiltered.jetty.Http(4000).filter(unfiltered.filter.Planify {
      case GET(Path(p)) => {
        val target = new File(siteTarget, p)
        if (target.exists) 
          p match {
            case Mime(mimeType) =>
              ContentType(mimeType) ~> ResponseFile(target) 
            case _ if target.isDirectory =>
              Redirect(pathWithoutTrailingSlash(p) + "/index.html")
            case _ =>
              ResponseFile(target)
          }
        else
          NotFound ~> ResponseString("File Not Found")
      }
    })

  case class ResponseFile(content: File) extends ResponseStreamer {
    def stream(os: OutputStream) = IO.transfer(content, os)
  }
  
  def pathWithoutTrailingSlash(path: String): String = 
    if(path.endsWith("/")) path.substring(0, path.size - 1)
    else path
}
