# Release Process Outline

This project uses GitFlow. PRs should be merged onto `develop`. New releases prepared from `release/vX.Y.Z` branch. `master` updated from `release/...` branches.

1. Run `git clean -fdx`. This makes sure there are no unexpected dependencies or artifacts that could affect the build.
2. Create a branch to do the release work on. Something like `release/<X>.<Y>.<Z>` is good.
3. Write release notes in Markdown with filename  `notes/<X>.<Y>.markdown`. Go through the commit logs and collect the major new features, bug fixes, deprecations, and anything else relevant to users. Making note of breaking changes is particularly important.
4. Run `git tag -u <GPG key id> v<X>.<Y>.<Z> && git push`. This creates the tag that the `sbt-git` plugin will use to extract the artifact version number and publishes it. Providing a GPG key is just good form. [Here's some documention](http://www.dewinter.com/gnupg_howto/english/GPGMiniHowto-3.html) on how to get set up to have one.
5. Run `sbt scripted publish`. Executes the tests first and then stages the binary in Bintray. [See SBT documentation](http://www.scala-sbt.org/0.13/docs/Bintray-For-Plugins.html) on how to get set up with Bintray.
6. Confirm the plugin is properly staged for release on Bintray.  The Bintray page for sbt-site is [here](https://bintray.com/sbt/sbt-plugin-releases/sbt-site/view).
7. Run `sbt bintrayRelease`. This moves the plugin from the staged release to published release on Bintray.
8. Update `git.baseVersion` to next release.
9. Update `README.md` to reference new binary version.
10. Commit and push.
11. Edit Bintray info to point to release notes on Github.
12. Create a release entry in GitHub
