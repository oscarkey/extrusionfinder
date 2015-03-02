Extrusion Finder
================

---

Description
------------
Did your favourite extrusion break? Are you in dire need of a Triple Wall H-Channel or a 2-inch Dunnage Retainer?

Fear not, Extrusion Finder has the solution to your problems! With the click of a button, this state-of-the-art software can swiftly scour online extrusion manufacturers for exactly the part you are looking for. All you need is an Android phone with a camera, the shape of the extrusion, and ideal lighting conditions, and you are all set.

---

Installation
-------------

Android app: Install from source in extrusionfinder/client. Supports Android 4.1+ (Jelly Bean or later).

---

Usage
------

Take a photo. An overlay highlights what the application is currently interpreting as the extrusion. The application will return a list of matched extrusions, sorted by likeness. Click one of them to see more details and an order link.

---

Building the Server
-------------

Prerequisites
-  Java Development Kit 8 ([http://www.oracle.com/technetwork/java/index.html](http://www.oracle.com/technetwork/java/index.html))
-  Gradle 2.3 ([https://gradle.org/](https://gradle.org/))
-  Android Studio ([http://developer.android.com/tools/studio/index.html](http://developer.android.com/tools/studio/index.html])
-  MongoDB ([http://www.mongodb.org/](http://www.mongodb.org/))
-  Inkscape ([https://inkscape.org/en/](https://inkscape.org/en/])
-  Java server e.g. Jetty ([http://eclipse.org/jetty/](http://eclipse.org/jetty/))

Gradle will automatically download all other dependencies. Note that the OpenCV dependency only supports OSX and Linux

To do a complete build of the server and run unit tests, run the following command in folder extrusionfinder/server. This places a web archive at build/libs/extrusionFinder-1.0.war.

	gradle build

To compile the Javadoc documentation (the documentation html files can be found in build/docs/javadoc), run

	gradle javadoc

To run the unit tests, run

	gradle test

To run the integration tests (might take a while), run

	gradle integration

To compile and run the Part Sourcer, which populates the database:

	gradle jar
    java -jar build/libs/crawler.jar

To run on a server, the web archive needs the name "extrusionFinder.war"


Configuration settings, such as the address of the Mongo server and the path to Inkscape can be set in Configuration.java

---

Contents
---------
-  **client/app/src/main/java** contains the source code of the Android application
-  **client/app/src/androidTest/java** contains the tests for the Android code
-  **server/src/main/java** contains the source code of the server. The primary packages are:
   -  *configuration*: contains static values that can be changed before compilation to tweak the performance/behaviour of the program.
   -  *database*: implementation of database access.
   -  *imagedata*: definitions of internal image representation used during image processing and matching.
   -  *imagematching*: class that determines how similar images are to each other using their Zernike Moment representation.
   -  *orchestration*: class that handles the full image processing and matching pipeline.
   -  *parts*: definitions of internal data representation of extrusion parts, sizes, and manufacturers.
   -  *preprocessor*: implementation of preprocessing algorithm, which cleans up images for matching.
   -  *servlet*: server endpoint that receives client requests and responds.
   -  *sourcer*: standalone crawler for finding information about extrusion parts online.
   -  *zernike*: classes that compute the Zernike Moment representation of images.
-  **server/src/test/java** contains all the unit tests and integration tests of the server code
