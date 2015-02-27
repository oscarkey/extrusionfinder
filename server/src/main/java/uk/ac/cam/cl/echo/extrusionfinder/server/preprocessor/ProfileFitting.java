package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.GrayscaleImageData;

import java.awt.geom.Point2D;

/**
 * Finds profile centre and radius.
 */
public class ProfileFitting {
    static {
        System.out.println("here is a loading message definitely produced by me");
        try {
            nu.pattern.OpenCV.loadShared();
        } catch (Throwable e) {
            System.out.println("actually, was already loaded. for grep: loading");
        }
    }

    private int width;
    private int height;
    private Point centre;
    private double size;

    /**
     * Create a fitting for a given input grayscale image.
     *
     * @param input The image of the detected profile to find the centre and radius of.
     */
    public ProfileFitting(GrayscaleImageData input) {
        process(input);
    }

    private void process(GrayscaleImageData input) {
        Mat imageStep;

        width = input.width;
        height = input.height;

        Mat imageIn = new Mat(height, width, CvType.CV_8UC1);
        imageIn.put(0, 0, input.data);

        // Find image size and centre.
        centre = findCentre(imageIn);
        size = determineSize(imageIn, centre);
    }

    /**
     * Return the location of the centre for the Zernike Moment operations.
     *
     * @return The centre for Zernike Moment calculations.
     */
    public Point2D getCentre() {
        return new Point2D.Double(centre.x, centre.y);
    }

    /**
     * Return the radius for the Zernike Moment operations.
     *
     * @return The raidus of the circle for Zernike Moment calculations.
     */
    public double getRadius() {
        return size;
    }

    /**
     * Finds the centre of a given input image.
     * <p>
     * This implementation uses centre of mass.
     */
    private Point findCentre(Mat input) {
        Moments m = Imgproc.moments(input, true);

        return new Point(m.get_m10() / m.get_m00(), m.get_m01() / m.get_m00());
    }

    /**
     * Finds the image's current size.
     * <p>
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
}
