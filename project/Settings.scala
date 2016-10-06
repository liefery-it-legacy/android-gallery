import android.Keys._
import sbt._
import sbt.Keys._

object Settings {
    val common = Def.settings(
        autoScalaLibrary := false,
        minSdkVersion := "14",
        organization := "com.liefery.android.gallery",
        platformTarget := "android-24",
        resolvers += "jitpack" at "https://jitpack.io",
        scalaVersion := "2.11.8"
    )
}