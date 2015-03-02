Extrusion Finder
================

---

Description:
------------
Did your favourite extrusion break? Are you in dire need of a Triple Wall H-Channel or a 2-inch Dunnage Retainer?

Fear not, Extrusion Finder has the solution to your problems! With the click of a button, this state-of-the-art software can swiftly scour online extrusion manufacturers for exactly the part you are looking for. All you need is an Android phone with a camera, the shape of the extrusion, and ideal lighting conditions, and you are all set.

---

Installation:
-------------

How to install on Android phone?  
To use the application, you need a phone with a camera and Android v.4.4+.

---

Usage:
------

Pictures?

1.  The application will activate your phone's camera. Take a picture of the extrusion you want to find online. Make sure the extrusion is centered in the picture, the image is focused, and that the picture is taken as directly in front of the shape as possible. Click the round capture button to send the image.
2.  Extrusion Finder will take you to a results page with the similar extrusions we could find online, sorted in order of likeness.
3.  Click on an extrusion image that you think is fitting. You can see the details about the extrusion.
4.  If the extrusion matches, click the order link at the bottom, which will take you to the website's order page!

Yes, it is _that_ easy.

---

Development:
-------------

For development on the project, you need the following:
-  Java Development Kit 8 for compiling and running the project ([http://www.oracle.com/technetwork/java/index.html](http://www.oracle.com/technetwork/java/index.html))
-  Gradle v.2.3 for building the project ([https://gradle.org/](https://gradle.org/))
-  Android Studio v.?.?
-  MongoDB
-  Not a Windows machine (openCV?)

To do a complete build of the project and run unit tests, run the following command in a terminal:

	gradle build

To compile the Javadoc documentation (the documentation html files can be found in documentation/), run

	gradle javadoc

To run the unit tests, run

	gradle test

To run the integration tests (might take a while), run

	gradle integration

To compile and run the Part Sourcer (crawler that populates database), run

	gradle jar
    java -jar build/libs/crawler.jar

---

Contents:
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
