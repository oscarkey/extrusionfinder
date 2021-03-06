package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.GrayscaleImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Extracts extrusion cross-section profile.
 */
public class ProfileDetector {
    static {
        try {
            nu.pattern.OpenCV.loadShared();
        } catch (Throwable e) {
            // Assume library already loaded, and ignore the error
        }
    }

    /**
     * Creates a profile detector.
     * <p>
     * The detector can be reused for different input images.
     */
    public ProfileDetector() {
        // If it turns out that this class is super slow, we could allocate stuff for in-place
        // operations here.
    }

    /**
     * Produces a grey scale image of only the extrusion's profile.
     *
     * @param input An input photograph or scan containing extrusion.
     * @return A newly allocated greyscale image of the extrusion profile.
     */
    public GrayscaleImageData process(RGBImageData input) {
        int diameter = Configuration.PROFILE_DETECTION_STANDARD_IMAGE_SIZE;
        int blurDiameter = Configuration.PROFILE_DETECTION_STANDARD_BILATERAL_FILTER_BLUR_DIAMETER;
        int sigma = Configuration.PROFILE_DETECTION_STANDARD_BILATERAL_FILTER_SIGMA;

        Mat imageStep;

        Mat imageIn = new Mat(input.height, input.width, CvType.CV_8UC3);
        imageIn.put(0, 0, input.data);

        // Raw image, but resized to given diameter.
        Mat rawImage = standardiseInput(imageIn, diameter);

        // Compute a mask by using adaptivate thresholding.
        Mat thresholdMask;
        imageStep = rawImage;
        imageStep = blur(imageStep, blurDiameter, sigma, sigma);
        imageStep = minGreyscale(imageStep);
        imageStep = invert(imageStep);
        imageStep = fade(imageStep);
        imageStep = threshold(imageStep);
        thresholdMask = imageStep;

        // Calculate the contours using the threshold mask.
        Mat contourMask;
        imageStep = thresholdMask;
        imageStep = largestContourMask(imageStep);
        contourMask = imageStep;

        // Extract profile from original standardised image, using masks.
        Mat profile;
        imageStep = rawImage;
        imageStep = avgGreyscale(imageStep);
        // Does the threshold mask need to be applied? Isn't this a subset of the contour mask?
        imageStep = applyBinaryMask(imageStep, thresholdMask);
        imageStep = applyBinaryMask(imageStep, contourMask);
        profile = imageStep;

        // TODO: Add histogram equalisation.

        Mat imageOut = profile;

        byte[] outData = new byte[imageOut.rows() * imageOut.cols()];
        imageOut.get(0, 0, outData);
        return new GrayscaleImageData(outData, imageOut.cols(), imageOut.rows());
    }

    /**
     * Returns a standardised version of the input image.
     * <p>
     * The returned image has both width and height equal equal to specified diameter.
     * The image will be cropped to a square and then scaled.
     */
    private static Mat standardiseInput(Mat input, int diameter) {
        int oldWidth = input.cols();
        int oldHeight = input.rows();

        Mat cropped;
        if (oldWidth > oldHeight) {
            int cropAmount = (oldWidth - oldHeight) / 2;
            Rect region = new Rect(cropAmount, 0, oldHeight, oldHeight);
            cropped = new Mat(input, region);
        } else if (oldWidth < oldHeight) {
            int cropAmount = (oldHeight - oldWidth) / 2;
            Rect region = new Rect(0, cropAmount, oldWidth, oldWidth);
            // region = new Rect(0, 0, oldWidth, oldHeight - 1);
            cropped = new Mat(input, region);
        } else {
            cropped = input;
        }

        Mat imageResized = new Mat();
        Size newSize = new Size(diameter, diameter);
        Imgproc.resize(cropped, imageResized, newSize);

        // Assuming RGB image already.
        return imageResized;
    }

    /**
     * Applies a bilateral blur to the given image.
     * <p>
     * The input image is blurred using supplied parameters.
     * <p>
     * See http://docs.opencv.org/modules/imgproc/doc/filtering.html#bilateralfilter and
     * http://www.dai.ed.ac.uk/CVonline/LOCAL_COPIES/MANDUCHI1/Bilateral_Filtering.html
     */
    private static Mat blur(Mat input, int blurDiameter, int sigmaColor, int sigmaSpace) {
        Mat output = new Mat();
        Imgproc.bilateralFilter(input, output, blurDiameter, sigmaColor, sigmaSpace);
        return output;
    }

    /**
     * Converts the input image to greyscale using the minimum component per pixel.
     * <p>
     * For each pixel, the greyscale value is given as min(pixel.red, pixel.green, pixel.blue).
     */
    private static Mat minGreyscale(Mat input) {
        // Extract individual channels.
        Mat c1 = new Mat(input.rows(), input.cols(), CvType.CV_8UC1);
        Mat c2 = new Mat(input.rows(), input.cols(), CvType.CV_8UC1);
        Mat c3 = new Mat(input.rows(), input.cols(), CvType.CV_8UC1);
        List<Mat> matSrc = new ArrayList<>(3);
        List<Mat> matDst = new ArrayList<>(3);
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
    private static Mat avgGreyscale(Mat input) {
        Mat output = new Mat();
        Imgproc.cvtColor(input, output, Imgproc.COLOR_RGB2GRAY);
        return output;
    }

    // TODO: change to be better normalised.
    /**
     * Applies a radial fade to black to the given image from the center.
     * <p>
     * The fade is linear, and the fading region is the circle, centred with the image, with double
     * the diameter.
     */
    private static Mat fade(Mat input) {
        int width = input.cols();
        int height = input.rows();
        double diameter = input.rows();
        byte[] bytes = new byte[width * height];
        Mat mask = new Mat(height, width, CvType.CV_8UC1);

        double r = diameter / 2.0;
        int i = 0;
        // The maximum fade strength of a pixel (current code => always sqrt(2)/2).
        double maximum = Math.hypot(r, r) / diameter;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, i++) {
                // How much of the pixel (fraction) to keep.
                double keep = maximum - (Math.hypot(x-r, y-r) / diameter);
                // How much of the pixel (fraction*255) to keep.
                bytes[i] = keep < 0 ? 0 : (byte)(keep * 255.0);
            }
        }

        mask.put(0, 0, bytes);

        return applyMask(input, mask);
    }

    /**
     * Applies an adaptive threshold to the given image.
     */
    private static Mat threshold(Mat input) {
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
    private static Mat largestContourMask(Mat input) {
        Mat output = new Mat(input.rows(), input.cols(), CvType.CV_8UC1);
        List<MatOfPoint> contours = new LinkedList<>();
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
            List<MatOfPoint> list = new ArrayList<>(1);
            list.add(largestContour);
            Imgproc.drawContours(output, list, 0, new Scalar(255), -1);
        }
        return output;
    }

    /**
     * Inverts the given image.
     */
    private static Mat invert(Mat input) {
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
    private static Mat applyBinaryMask(Mat input, Mat mask) {
        Mat binMask = new Mat();
        Core.min(mask.clone(), new Scalar(1), binMask);

        return applyMultiplicationMask(input, binMask, 1.0);
    }

    /**
     * Applies the given mask to the given image.
     * <p>
     * The mask is normalised and multiplied with the image. The mask must be single channel.
     */
    private static Mat applyMask(Mat input, Mat mask) {
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
    private static Mat applyMultiplicationMask(Mat input, Mat mask, double scale) {
        Mat output = new Mat(input.rows(), input.cols(), input.type());
        Mat compatibleMask;
        switch (input.channels()) {
            case 1:
                compatibleMask = mask;
                break;
            case 3: // Do the same as 4 channels
            case 4:
                compatibleMask = new Mat();
                Imgproc.cvtColor(mask, compatibleMask, Imgproc.COLOR_GRAY2RGB, input.channels());
                break;
            default:
                throw new UnsupportedOperationException("Number of channels must be 1, 3, or 4 for mask application.");
        }
        Core.multiply(input, compatibleMask, output, scale);
        return output;
    }
}
