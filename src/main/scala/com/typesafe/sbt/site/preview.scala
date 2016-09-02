package com.typesafe.sbt.site

import sbt._
import unfiltered.jetty.Server
import unfiltered.filter.Plan
import unfiltered.response._
import unfiltered.request._
import java.io.File
import java.io.OutputStream
import collection.mutable.Map

object Preview {
  def apply(port: Int, base: File, genSite: TaskKey[File], genSources: TaskKey[Seq[File]], state: State): Server = {
  	val (_, rootFile) = runTask(genSite, state)
  	val (_, rootSources) = runTask(genSources, state)

  	val rootPage = new URL(rootFile.toURI.toURL, "index.html") // TODO: which file to start with?
  	var mapSources: Map[File, Long] = updateSources(rootSources)

  	val plan: Plan = unfiltered.filter.Planify {
      case GET(unfiltered.request.Path(Seg(Nil))) =>
        responseStreamer(rootPage)
      case GET(unfiltered.request.Path(Seg(path))) => {
      	val (_, newSources) = runTask(genSources, state)
      	val newMapSources = updateSources(newSources)
      	if(mapSources != newMapSources) {
      		val _ = runTask(genSite, state)
      		mapSources = newMapSources
      	}
        val newFile: File = base / path.mkString("/")
        responseStreamer(newFile.toURI.toURL)
      }
    }
    val http = unfiltered.jetty.Server.local(port)
    http.plan(plan).resources(rootPage)
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

  def updateSources(files: Seq[File]): Map[File, Long] =
    Map(files.filter(!_.toString.endsWith("~")).map(file => file -> file.lastModified()): _*)

  def runTask[A](task: TaskKey[A], state: State): (State, A) = {
  	val extracted = Project extract state
  	extracted.runTask(task, state)
  }
}