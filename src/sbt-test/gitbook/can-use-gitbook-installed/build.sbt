import com.typesafe.sbt.site.GitbookSupport.{ Gitbook, gitbookInstallDir }

name := "test-gitbook"

site.settings

site.gitbookSupport()

gitbookInstallDir in Gitbook := Some(baseDirectory.value / "node_modules" / "gitbook")
