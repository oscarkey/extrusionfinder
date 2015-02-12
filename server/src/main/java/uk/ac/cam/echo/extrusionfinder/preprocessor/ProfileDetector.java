package uk.ac.cam.echo.extrusionfinder.preprocessor;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;

// Note: Currently extends ImageModifier for the purposes of testing.

/**
 * Extracts extrusion cross-section profile.
 */
public class ProfileDetector extends ImageModifier {
    private static final int imageSize = 200;

    public ProfileDetector(String in, String out) {
        super(in, out);
    }

    class IntPoint {
        int x;
        int y;
        IntPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    };

    @Override
    protected void process() {
        int diameter = imageSize;
        int blurDiameter = 8;
        int sigma = 15;

        Mat imageStep;

        // Raw image, but resized to given diameter.
        Mat rawImage;
        rawImage = standardiseInput();

        Mat thresholdMask;
        imageStep = imageRaw;
        imageStep = blur(imageStep, blurDiameter, sigma, sigma);
        imageStep = minGreyscale(imageStep);
        imageStep = tunnelVision(imageStep);
        imageStep = threshold(imageStep);
        thresholdMask = imageStep;

        Mat contourMask;
        imageStep = thresholdMask;
        imageStep = largestContour(imageStep);
        contourMask = imageStep;

        Mat profile;
        imageStep = imageRaw;
        imageStep = avgGreyscale(imageStep);
        imageStep = applyMask(imageStep, thresholdMask);
        imageStep = applyMask(imageStep, contourMask);
        profile = imageStep;

        imageOut = profile;
    }

    /**
     * Returns a standardised {@link #this.imageIn} image.
     * <p>
     * The returned image has both width and height equal to {@link #this.diameter}.
     */
    private Mat standardiseInput() {
        Mat imageResized = new Mat();
        Mat output = new Mat();
        Size newSize = new Size(diameter, diameter);
        Imgproc.resize(imageIn, imageResized, newSize);

        // Assuming RGB image already.
        output = imageResized;
        // Imgproc.cvtColor(imageResized, output, Imgproc.COLOR_RGBA2RGB);

        return output;
    }

    /**
     * Applies a bilateral blur to the given image.
     * <p>
     * TODO.
     */
    private Mat blur(Mat input, int blurDiameter, int sigmaColor, int sigmaSpace) {
        Mat output = new Mat();
        Imgproc.bilateralFilter(input, output, blurDiameter, sigmaColor, sigmaSpace);
        return input;
    }

    /**
     * Converts the input image to greyscale using the minimum component per pixel.
     * <p>
     * For each pixel, the greyscale value is given as min(pixel.red, pixel.green, pixel.blue).
     */
    private Mat minGreyscale(Mat input) {
        // Extract individual channels.
        Mat c1 = new Mat(diameter, diameter, CvType.CV_8UC1);
        Mat c2 = new Mat(diameter, diameter, CvType.CV_8UC1);
        Mat c3 = new Mat(diameter, diameter, CvType.CV_8UC1);
        List mats = new ArrayList(3);
        mats.add(c1);
        mats.add(c2);
        mats.add(c3);
        Imgproc.split(input, mats);

        // Get minimums
        Mat output;
        output = c1;
        Core.min(output, c2, output);
        Core.min(output, c3, output);

        return output;
    }

    /**
     * Converts the input image to greyscale using the averaging of all components per pixel.
     * <p>
     * For each pixel, the greyscale value is given as (pixel.red + pixel.green + pixel.blue) / 3.
     */
    private Mat avgGreyscale(Mat input) {
        Mat output = new Mat();
        Imgproc.cvtColor(input, output, Imgproc.COLOR_RGB2GRAY);
        return output;
    }

    /**
     * Applies a fade to black to the given image with radial distance from the center.
     * <p>
     * The fade is linear, and the fading region is the maximal circle bound by the image.
     */
    private Mat tunnelVision(Mat input) {
        byte[] = new byte[diameter * diameter];
        Mat mask = new Mat(diameter, diameter, CvType.CV_8UC1
        return applyMask(input, mask);
    }

    /**
     * Applies an adaptive threshold to the given image.
     */
    private Mat threshold(Mat input) {
        return input;
    }

    /**
     * Finds the largest contour of the given image and returns its mask.
     */
    private Mat largestContour(Mat input) {
        return input;
    }

    /**
     * Applies the given binary mask to the given image.
     * <p>
     * All pixels with non-zero values in mask are preserved, otherwise they are reduced to black.
     */
    private Mat applyBinaryMask(Mat input, Mat mask) {
        Mat output = new Mat();
        return input.copyTo(input, mask);
    }

    /**
     * Applies the given binary mask to the given image.
     * <p>
     * The input and mask are multiplied together. The mask must be single channel.
     */
    private Mat applyMultiplicationMask(Mat input, Mat mask) {
        // Mat output = input.clone();
        // Mat multiMask = new Mat();

        // return output.copyTo(input, mask);
        return input;
    }

    public static void main(String[] args) {
        new ProfileDetector(args[0], args[1]);
    }
}

