name := "test"
version := "0.0-ABCD"

publishTo := Some(Resolver.file("file", file("target/release")))
//#publishSite
publishSite
//#publishSite

def artifactFlavour = Def.task {
  s"${name.value}_${scalaBinaryVersion.value}"
}

TaskKey[Unit]("checkPackageSite") := {
  val siteZipName = s"${artifactFlavour.value}-${version.value}-site.zip"
  val siteZipFile = packageSite.value

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
  val publishedZipFile = file("target/release") / name.value / artifactFlavour.value / version.value / packageSite.value.getName
  checkFileContent(publishedZipFile, packageSite.value)
}

def checkFileContent(actual: File, expected: File) = {
  assert(actual.exists, s"${actual.getAbsolutePath} did not exist")
  val isTheSame = IO.readBytes(actual).sameElements(IO.readBytes(expected))
  assert(isTheSame, s"${actual.getAbsolutePath} did not contain the same content as ${expected.getAbsolutePath}")
}
