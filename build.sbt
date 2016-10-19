import sbt.Keys._

lazy val gallery = project.in( file( "." ) )
    .settings( androidBuildAar ++ Settings.common )
    .settings(
        javacOptions ++=
            "-source" :: "1.7" ::
            "-target" :: "1.7" ::
            Nil,
        libraryDependencies ++=
            "com.android.support" % "appcompat-v7" % "24.2.1" ::
            "com.github.chrisbanes" % "PhotoView" % "1.3.0" ::
            "com.github.google" % "flexbox-layout" % "0.2.3" ::
            "com.github.jkwiecien" % "EasyImage" % "1.3.1" ::
            "com.github.square.picasso" % "picasso" % "eac9b2b330" ::
            Nil,
        name := "gallery",
        publishArtifact in ( Compile, packageDoc ) := false
    )

lazy val sample = project
    .androidBuildWith( gallery )
    .settings( Settings.common )
    .settings(
        organization := organization.value + ".sample",
        run <<= run in Android
    )