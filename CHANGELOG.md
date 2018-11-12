# Changelog

## 1.5.5

_2018-11-12_

 * Don't include deleted photos in getImages and getPaths

## 1.5.4

_2018-07-17_

 * Request permissions in `ActionActivity` rather than an Aux Fragment

## 1.5.3

_2018-07-11_

 * Add camera permission and runtime handling

## 1.5.2

_2018-07-09_

 * Add new `OnPhotoErrorListener`

## 1.5.1

_2018-07-04_

 * Move images to internal app storage after taking a photo
 * Upgrade to photo-view 2.1.4

## 1.5.0

_2018-06-28_

 * Introduce `ResultHandler` to improve usage of `ActionActivity` directly

## 1.4.2

_2018-06-05_

 * Upgrade to scala 2.11.12
 * Upgrade to flexbox 1.0.0

## 1.4.1

_2018-04-26_

 * Fix crash on old API levels due to missing 'app' xml prefix

## 1.4.0

_2018-04-24_

 * Upgrade to glide 4.7.1
 * Upgrade to android platform 27
 * Upgrade to sbt 0.13.17

## 1.3.0

_2018-01-25_

 * Integrate runtime permission handling into library core

## 1.2.2

_2018-01-18_

 * Add `OnPhotoAddedListener` and `OnPhotoRemovedListener`

## 1.2.1

_2018-01-18_

 * Add subtle elevation to add photo button
 * Set add photo button icon color to `textColorPrimaryInverse`

## 1.2.0

_2018-01-17_

 * Show "add photo" button as thumbnail
 * Give each class a proper suffix for easier identification
 * Upgrade to support-library 26.1.0
 * Upgrade to glide 4.3.1
 * Upgrade to easy-image 2.0.4
 * Upgrade to flexbox 0.3.2
 * Upgrade to sbt-android 1.7.10

## 1.1.1

_2017-08-31_

 *  Upgrade to support-library 26.0.2
 *  Upgrade to flexbox 0.3.0
 *  Upgrade to glide 4.0.0
 *  Upgrade to sbt-android 1.7.9
 *  Upgrade to sbt 0.13.16

## 1.1.0

_2017-06-19_

 * Upgrade to Flexbox 0.2.7
 * Using Glide 4.0.0-RC0 instead of Picasso for image loading

## 1.0.7

_2017-05-18_

 * Upgrade to EasyImage 2.0.2  
   `minSdkVersion` had to be raised to 16 accordingly

## 1.0.6

_2017-05-18_

 * Upgrade to PhotoView 2.0.0
 * Upgrade to appcompat-v7 25.3.1
 * Upgrade to flexbox 0.2.6
 * Upgrade to sbt 0.13.15
 * Upgrade to sbt-android 1.7.7
 * Upgrade to scala 2.11.11

## 1.0.5

_2016-12-19_

 * Upgrade to Android support library 25.1.0
 * Upgrade to picasso 289ed30

## 1.0.4

_2016-12-09_

 * Downsample images before rotating (fixes OOM on Samsung Galaxy S3)
 * Upgrade to flexbox 0.2.5
 * Upgrade to sbt-android 1.7.2

## 1.0.3

_2016-11-29_

 * Fix image orientation on certain old devices

## 1.0.2

_2016-11-24_

 * Limit image resolution to screen size in Detail screen

## 1.0.1

_2016-11-24_

 * Remove camera permission (camera is not used by the app itself)
 * Add subtle background to Detail screen Toolbar to improve trash icon visibility
 * Improve error handling to prevent crashes
 * Fix sample app crash on old devices due to icons Drawables

## 1.0.0

_2016-11-18_

 * Initial release