package com.typesafe.sbt.site

import sbt._, Keys._
import unfiltered.jetty.Server
import unfiltered.filter.Plan
import unfiltered.response._
import unfiltered.request._
import java.io.File
import java.io.OutputStream
import collection.mutable.Map

object Preview {
  def apply(port: Int, base: File, genSite: TaskKey[File], genSources: State => Seq[File], state: State): Server = {
    val rootFile = runTask(genSite, state)
    val rootSources = genSources(state)

    val rootPage: Option[URL] = startPageURL(rootFile)
    var mapSources: Map[File, Long] = mapFileToLastModified(rootSources)

    val plan: Plan = unfiltered.filter.Planify {
      case GET(unfiltered.request.Path(Seg(path))) => {
        val newSources = genSources(state)
        val newMapSources = mapFileToLastModified(newSources)
        if(mapSources != newMapSources) {
          val _ = runTask(genSite, state)
          mapSources = newMapSources
        }
        path match {
          case p :: ps =>
            val newFile: File = base / path.mkString("/")
            responseStreamer(newFile.toURI.toURL)
          case Nil =>
            rootPage match {
              case Some(startingPage) => responseStreamer(startingPage)
              case None => ResponseString("No file found, make sure to generate any starting web page at root project (default: \"index.html\")")
            }
        }
      }
    }
    val http = unfiltered.jetty.Server.local(port)
    http.plan(plan).resources(new URL(rootFile.toURI.toURL, "."))
  }

  def startPageURL(rootFile: File): Option[URL] = {
    val files = rootFile.listFiles.filter(!_.isDirectory)
    files.toList.filter(_.getName == "index.html") match {
      case ind :: Nil => Some(ind.toURI.toURL)
      case Nil if(files.length > 0) => Some(files(0).toURI.toURL)
      case _ => None
    }
  }

  def responseStreamer(url: URL) =
    new ResponseStreamer { def stream(os:OutputStream): Unit = {
      val is = url.openStream
      try {
        val buf = new Array[Byte](1024)
        Iterator.continually(is.read(buf)).takeWhile(_ != -1)
          .foreach(os.write(buf, 0, _))
      } finally {
        is.close
      }
    } }

  def mapFileToLastModified(files: Seq[File]): Map[File, Long] =
    Map(files.filter(!_.toString.endsWith("~")).map(file => file -> file.lastModified()): _*)

  def runTask[A](task: TaskKey[A], state: State): A = {
    val extracted = Project extract state
    val (_, result) = extracted.runTask(task, state)
    result
  }
}