import sbtcompat.PluginCompat

scalaVersion := "2.12.20"

name := "test"
version := "0.0-ABCD"

publishTo := Some(Resolver.file("file", file("target/release")))
//#publishSite
publishSite()
//#publishSite

def artifactFlavour = Def.task {
  s"${name.value}_${scalaBinaryVersion.value}"
}

TaskKey[Unit]("checkPackageSite") := {
  implicit val conv: xsbti.FileConverter = fileConverter.value
  val siteZipName = s"${artifactFlavour.value}-${version.value}-site.zip"
  val siteZipFile = PluginCompat.toFile(packageSite.value)

  assert(siteZipFile.exists, s"${siteZipFile.getAbsolutePath} did not exist")
  assert(siteZipFile.getName == siteZipName, s"${siteZipFile.getName} did not match expected '$siteZipName")

  val dir = siteSourceDirectory.value
  val expectedFiles = Seq("README.html").map(path => path -> (dir / path)).toMap
  val zippedDir = file("target/unzipped-site")
  IO.delete(zippedDir)
  val zippedFiles = IO.unzip(siteZipFile, zippedDir)

  assert(expectedFiles.size == zippedFiles.size)
  for (actualFile <- zippedFiles) {
    val actualPath = IO.relativize(zippedDir, actualFile).get
    assert(expectedFiles.isDefinedAt(actualPath), s"$actualPath not expected in ${siteZipFile}")
    checkFileContent(actualFile, expectedFiles(actualPath))
  }
}

TaskKey[Unit]("checkPublishSite") := {
  implicit val conv: xsbti.FileConverter = fileConverter.value
  val siteZipFile = PluginCompat.toFile(packageSite.value)
  val publishedZipFile = file("target/release") / name.value / artifactFlavour.value / version.value / siteZipFile.getName
  checkFileContent(publishedZipFile, siteZipFile)
}

def checkFileContent(actual: File, expected: File) = {
  assert(actual.exists, s"${actual.getAbsolutePath} did not exist")
  val isTheSame = IO.readBytes(actual).sameElements(IO.readBytes(expected))
  assert(isTheSame, s"${actual.getAbsolutePath} did not contain the same content as ${expected.getAbsolutePath}")
}
