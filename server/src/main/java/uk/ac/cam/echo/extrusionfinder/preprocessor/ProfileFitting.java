package uk.ac.cam.echo.extrusionfinder.preprocessor;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.highgui.Highgui;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

import uk.ac.cam.echo.extrusionfinder.imagedata.GrayscaleImageData;

/**
 * Finds profile centre and radius.
 */
public class ProfileFitting {
    private int width;
    private int height;
    private Point centre;
    private double size;

    public ProfileFitting(GrayscaleImageData input) {
        // Ensure library is loaded.
        System.loadLibrary("opencv_java249");

        process(input);
    }

    private void process(GrayscaleImageData input) {
        Mat imageStep;

        width = input.width;
        height = input.height;

        Mat imageIn = new Mat(width, height, CvType.CV_8UC1);
        imageIn.put(0, 0, input.data);

        // Find image size and centre.
        centre = findCentre(imageIn);
        size = determineSize(imageIn, centre);
    }

    /**
     * Return the location of the centre for the Zernike Moment operations.
     */
    public Point2D getCentre() {
        return new Point2D.Double(centre.x, centre.y);
    }

    /**
     * Return the radius for the Zernike Moment operations.
     */
    public double getRadius() {
        return size;
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

    public static void main(String[] args) {
        System.loadLibrary("opencv_java249");

        Mat in = avgGreyscale(Highgui.imread(args[0]));
        byte[] inData = new byte[in.rows() * in.cols()];
        in.get(0, 0, inData);

        ProfileFitting fitting = new ProfileFitting(new GrayscaleImageData(inData, in.rows(), in.cols()));

        Point2D centre = fitting.getCentre();
        System.out.printf("Centre: (%s, %s)\nSize: %s\n", centre.getX(), centre.getY(), fitting.getRadius());
    }
}
