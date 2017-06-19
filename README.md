# Android Gallery

> A simple gallery widget with image capturing

[![](https://jitpack.io/v/liefery/android-gallery.svg)](https://jitpack.io/#liefery/android-gallery)

[![Preview animation](https://liefery.github.io/android-gallery/preview.jpg)](https://www.youtube.com/watch?v=DTpj6bi7jcc)

## Installation

### sbt

```scala
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.liefery" % "android-gallery" % "1.1.0"
```

### Gradle

```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    compile 'com.github.liefery:android-gallery:1.1.0'
}
```

## Usage

Please have a look at the sample application (including runtime permission handling).
