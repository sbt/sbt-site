package com.typesafe.sbt.site

import sbt._
import unfiltered.jetty.Server
import unfiltered.filter.Plan
import unfiltered.response._
import unfiltered.request._
import java.io.File
import java.io.OutputStream

object Preview {
  def apply(port: Int, rootFile: File, base: File): Server = {
  	val plan: Plan = unfiltered.filter.Planify {
      case GET(unfiltered.request.Path(Seg(Nil))) =>
        responseStreamer(new URL(rootFile.toURI.toURL, "index.html")) // TODO what file to start with?
      case GET(unfiltered.request.Path(Seg(path))) => {
        val newFile: File = base / path.mkString("/")
        responseStreamer(newFile.toURI.toURL)
      }
    }
    val http = unfiltered.jetty.Server.local(port)
    http.plan(plan).resources(new URL(rootFile.toURI.toURL, "index.html"))
  }

  def responseStreamer(url: URL) =
    new ResponseStreamer { def stream(os:OutputStream) { 
      val is = url.openStream
      try {
        val buf = new Array[Byte](1024)
        Iterator.continually(is.read(buf)).takeWhile(_ != -1)
          .foreach(os.write(buf, 0, _))
      } finally {
        is.close
      }
    } }
}