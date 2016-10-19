import android.Keys._
import sbt._
import sbt.Keys._

object Settings {
    val common = Def.settings(
        autoScalaLibrary := false,
        javacOptions ++=
            "-source" :: "1.7" ::
            "-target" :: "1.7" ::
            Nil,
        minSdkVersion := "14",
        organization := "com.liefery.android.gallery",
        platformTarget := "android-24",
        resolvers += "jitpack" at "https://jitpack.io",
        scalaVersion := "2.11.8"
    )
}