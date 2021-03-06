package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;

public class ImageDataLoaderTester {
    public static void main(String[] args) throws ImageLoadException {
        System.loadLibrary(Configuration.OPENCV_LIBRARY_NAME);

        RGBImageData rgb = RGBImageData.load(args[0]);
        rgb.save(args[1]);

        GrayscaleImageData grayscale = GrayscaleImageData.load(args[0]);
        grayscale.save(args[2]);

        RGBImageData rgbgrayscale = RGBImageData.load(args[2]);
        rgbgrayscale.save(args[3]);
    }
}
