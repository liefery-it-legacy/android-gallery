lazy val gallery = project.in( file( "." ) )
    .enablePlugins( AndroidLib )
    .settings( Settings.common )
    .settings(
        libraryDependencies ++=
            Dependencies.easyImage ::
            Dependencies.flexbox ::
            Dependencies.glide ::
            Dependencies.photoView ::
            Dependencies.supportAppcompat ::
            Nil,
        name := "gallery",
        publishArtifact in ( Compile, packageDoc ) := false,
        renderVectorDrawables := true,
        resolvers ++=
            ( "Google Maven" at "https://maven.google.com" ) ::
            ( "jitpack" at "https://jitpack.io" ) ::
            Resolver.jcenterRepo ::
            Nil
    )

lazy val sample = project
    .enablePlugins( AndroidApp )
    .settings( Settings.common )
    .settings(
        organization := organization.value + ".gallery.sample",
        run := ( run in Android ).evaluated
    )
    .dependsOn( gallery )