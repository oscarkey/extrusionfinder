package uk.ac.cam.echo.extrusionfinder.preprocessor;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;

// Note: Currently extends ImageModifier for the purposes of testing.

/**
 * Extracts extrusion cross-section profile.
 */
public class ProfileDetector extends ImageModifier {
    private static final int imageSize = 200;

    private int diameter;

    class IntPoint {
        int x;
        int y;
        IntPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    };

    public ProfileDetector(String in, String out) {
        super(in, out);
    }

    @Override
    protected void process() {
        diameter = imageSize;
        int blurDiameter = 8;
        int sigma = 15;

        Mat imageStep;

        // Raw image, but resized to given diameter.
        Mat rawImage;
        rawImage = standardiseInput();

        Mat thresholdMask;
        imageStep = rawImage;
        imageStep = blur(imageStep, blurDiameter, sigma, sigma);
        imageStep = minGreyscale(imageStep);
        imageStep = invert(imageStep);
        imageStep = fade(imageStep);
        imageStep = threshold(imageStep);
        thresholdMask = imageStep;

        Mat contourMask;
        imageStep = thresholdMask;
        imageStep = largestContourMask(imageStep);
        contourMask = imageStep;

        Mat profile;
        imageStep = rawImage;
        imageStep = avgGreyscale(imageStep);
        imageStep = applyBinaryMask(imageStep, thresholdMask);
        imageStep = applyBinaryMask(imageStep, contourMask);
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
        return output;
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
        List<Mat> matSrc = new ArrayList<Mat>(3);
        List<Mat> matDst = new ArrayList<Mat>(3);
        MatOfInt fromTo = new MatOfInt(0, 0, 1, 1, 2, 2);
        matSrc.add(input);
        matDst.add(c1);
        matDst.add(c2);
        matDst.add(c3);
        Core.mixChannels(matSrc, matDst, fromTo);

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
     * NOT TRUE!
     * <p>
     * The fade is linear, and the fading region is the circle, centred with the image, with double
     * the diameter.
     */
    private Mat fade(Mat input) {
        byte[] bytes = new byte[diameter * diameter];
        Mat mask = new Mat(diameter, diameter, CvType.CV_8UC1);

        int r = diameter / 2;
        int i = 0;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++, i++) {
                double keep = 0.707106781187 - (Math.sqrt((x-r)*(x-r) + (y-r)*(y-r)) / diameter);
                bytes[i] = (byte)(keep < 0 ? 0 : (byte)(keep * 255.0));
            }
        }

        mask.put(0, 0, bytes);

        return applyMask(input, mask);
    }

    /**
     * Applies an adaptive threshold to the given image.
     */
    private Mat threshold(Mat input) {
        Mat output = new Mat();
        Imgproc.adaptiveThreshold(
            input, 
            output, 
            255,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV,
            11,
            2);

        return output;
    }

    /**
     * Finds the largest contour of the given image and returns its mask.
     * <p>
     * The contour area has a mask value of 255, whilst non-contour area has a value of 0.
     * <p>
     * If no contours exist, the mask is all zeros.
     */
    private Mat largestContourMask(Mat input) {
        Mat output = new Mat(input.rows(), input.cols(), CvType.CV_8UC1);
        List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(
            input.clone(),
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint largestContour = null;
        double largestArea = 0;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > largestArea) {
                largestArea = area;
                largestContour = contour;
            }
        }

        output.setTo(new Scalar(0));
        if (largestContour != null) {
            // Could be done using the original list and an index.
            List<MatOfPoint> list = new ArrayList<MatOfPoint>(1);
            list.add(largestContour);
            Imgproc.drawContours(output, list, 0, new Scalar(255), -1);
        }
        return output;
    }

    /**
     * Inverts the given image.
     */
    private Mat invert(Mat input) {
        Mat output = new Mat(input.rows(), input.cols(), input.type());
        output.setTo(new Scalar(255));
        Core.subtract(output, input, output);
        return output;
    }

    /**
     * Applies the given binary mask to the given image.
     * <p>
     * All pixels with non-zero values in mask are preserved, otherwise they are reduced to black.
     */
    private Mat applyBinaryMask(Mat input, Mat mask) {
        Mat binMask = new Mat();
        Core.min(mask.clone(), new Scalar(1), binMask);

        return applyMultiplicationMask(input, binMask, 1.0);
    }

    /**
     * Applies the given mask to the given image.
     * <p>
     * The mask is normalised and multiplied with the image. The mask must be single channel.
     */
    private Mat applyMask(Mat input, Mat mask) {
        return applyMultiplicationMask(input, mask, 1.0 / 255.0);
    }

    /**
     * Applies the given mask to the given image by multiplying raw values.
     * <p>
     * The input and mask are multiplied together, and a scale is applied. The mask must be single
     * channel.
     * <p>
     * Note that the mask has values 0 to 255, so scaling is necessary to avoid overflow.
     */
    private Mat applyMultiplicationMask(Mat input, Mat mask, double scale) {
        Mat output = new Mat(input.rows(), input.cols(), input.type());
        Mat compatibleMask;
        switch (input.channels()) {
        case 3:
        case 4:
            compatibleMask = new Mat();
            Imgproc.cvtColor(mask, compatibleMask, Imgproc.COLOR_GRAY2RGB, input.channels());
            break;
        default:
            compatibleMask = mask;
            break;
        }
        Core.multiply(input, compatibleMask, output, scale);
        // return output.copyTo(input, mask);
        return output;
    }

    public static void main(String[] args) {
        new ProfileDetector(args[0], args[1]);
    }
}
