package uk.ac.cam.echo.extrusionfinder.preprocessor;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Moments;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

// Note: Currently extends ImageModifier for the purposes of testing.

/**
 * Extracts extrusion cross-section profile.
 */
public class ProfileFitter extends ImageModifier {
    int width;
    int height;

    public ProfileFitter(String in, String out) {
        super(in, out);
    }

    @Override
    protected void process() {
        Mat imageStep;

        // Raw image, but ensured greyscaled.
        Mat rawImage;
        rawImage = standardiseInput();

        width = rawImage.rows();
        height = rawImage.cols();

        // // Find image size and centre.
        Point centre = findCentre(rawImage);
        double size = determineSize(rawImage, centre);

        System.out.printf("Centre: (%s, %s)\nSize: %s\n", centre.x, centre.y, size);
    }

    /**
     * Returns a standardised {@link #this.imageIn} image.
     * <p>
     * The returned image is greyscaled.
     */
    private Mat standardiseInput() {
        return avgGreyscale(imageIn);
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
     * Finds the centre of a given input image.
     *
     * This implementation uses centre of mass.
     */
    private Point findCentre(Mat input) {
        Moments m = Imgproc.moments(input, true);

        return new Point(m.get_m10() / m.get_m00(), m.get_m01() / m.get_m00());
    }

    /**
     * Finds the image's current size.
     *
     * This is currently done based on area.
     */
    private double determineSize(Mat input, Point centre) {
        byte[] bytes = new byte[width*height];
        double xCentre = centre.x, yCentre = centre.y;
        double rWin = 0;

        input.get(0, 0, bytes);

        for (int y = 0, i = 0; y < height; y++) {
            for (int x = 0; x < width; x++, i++) {
                if (bytes[i] != 0) {
                    double rThis = (x - xCentre) * (x - xCentre) + (y - yCentre) * (y - yCentre);
                    if (rThis > rWin) {
                        rWin = rThis;
                    }
                }
            }
        }

        return Math.sqrt(rWin) + 2.0;
    }

    public static void main(String[] args) {
        new ProfileFitter(args[0], args[1]);
    }
}
