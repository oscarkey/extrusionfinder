package uk.ac.cam.echo.extrusionfinder.preprocessor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;

/**
 * Loads, processes, and saves an image.
 * <p>
 * Intended as a way to test image processing code using files.
 */
public abstract class ImageModifier {
    protected Mat imageIn;
    protected Mat imageOut;

    public ImageModifier(String in, String out) {
        System.loadLibrary("opencv_java249");
        pipe(in, out);
    }

    private void load(String in) {
        imageIn = Highgui.imread(in);
    }

    private void save(String out) {
        Highgui.imwrite(out, imageOut);
    }

    private void pipe(String in, String out) {
        load(in);
        process();
        save(out);
    }

    protected abstract void process();
}
