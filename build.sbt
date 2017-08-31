lazy val gallery = project.in( file( "." ) )
    .enablePlugins( AndroidLib )
    .settings( Settings.common )
    .settings(
        libraryDependencies ++=
            "com.android.support" % "appcompat-v7" % "25.3.1" ::
            "com.github.chrisbanes" % "PhotoView" % "2.0.0" ::
            "com.github.jkwiecien" % "EasyImage" % "2.0.2" ::
            "com.github.bumptech.glide" % "glide" % "4.0.0" ::  
            "com.google.android" % "flexbox" % "0.2.7" ::
            Nil,
        name := "gallery",
        publishArtifact in ( Compile, packageDoc ) := false,
        renderVectorDrawables := true,
        resolvers += Resolver.jcenterRepo
    )

lazy val sample = project
    .enablePlugins( AndroidApp )
    .settings( Settings.common )
    .settings(
        organization := organization.value + ".gallery.sample",
        run := ( run in Android ).evaluated
    )
    .dependsOn( gallery )