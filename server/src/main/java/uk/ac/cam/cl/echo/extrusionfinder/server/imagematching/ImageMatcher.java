package uk.ac.cam.cl.echo.extrusionfinder.server.imagematching;

import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.Zernike;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageMatcher {

    //The Zernike moments of the ImageMatcher
    private final double[] zm;

    /**
     * Constructs an ImageMatcher with the Zernike moments computed using the img,
     * degree, center and radius parameters.
     *
     * @param image  the image to compute Zernike moments for
     * @param degree the degree to which to compute the Zernike moments to
     * @param center the point at which to take Zernike moments from
     * @param radius the radius of the circle to use
     */
    public ImageMatcher(BufferedImage image, int degree, Point2D center, double radius) {
        zm = Zernike.zernikeMoments(image, degree, center, radius);
    }

    /**
     * Constructs an ImageMatcher with the Zernike moments computed using the img,
     * degree, center and radius parameters.
     *
     * @param image  the image to compute Zernike moments for
     * @param degree the degree to which to compute the Zernike moments to
     * @param center the point at which to take Zernike moments from
     * @param radius the radius of the circle to use
     */
    public ImageMatcher(GrayscaleImageData image, int degree, Point2D center, double radius) {
        zm = Zernike.zernikeMoments(image.data, degree, center, radius);
    }

    /**
     * Computes the Euclidean distance between an n-dimensional vector and this.zm.
     *
     * @param zm the Zernike moments to be compared with
     * @return the Euclidean distance between this.zm and zm
     */
    public double compare(double[] zm) {
        return distance(this.zm, zm);
    }

    /**
     * Computes the Euclidean distance between 2 vectors of the same length. If one of the vectors has
     * more dimensions than the other, they are ignored.
     *
     * @param A an n-dimensional vector
     * @param B an n-dimensional vector
     * @return the Euclidean distance between the two vectors.
     */
    static double distance(double[] A, double[] B) throws IllegalArgumentException {
        if (A.length != B.length) {
            throw new IllegalArgumentException("Vectors must be the same length");
        }

        double x = 0;
        for (int i = 0; i < A.length; i++) {
            x += Math.pow(A[i] - B[i], 2);
        }
        return Math.sqrt(x);
    }

    /**
     * Computes the Zernike moments of the two images and returns the Euclidean
     * distance between them.
     *
     * @param image0   an image to be compared
     * @param image1   an image to be compared
     * @param degree the degree to which to compute the Zernike moments to
     * @param center the point at which to take Zernike moments from
     * @param radius the radius of the circle to use
     * @return the Euclidean distance between the Zernike moments of the two images
     */
    public static double compare(BufferedImage image0, BufferedImage image1, int degree, Point2D center, double radius) {
        double[] zm0 = Zernike.zernikeMoments(image0, degree, center, radius);
        double[] zm1 = Zernike.zernikeMoments(image1, degree, center, radius);

        return distance(zm0, zm1);
    }

    /**
     * Reads the images from their URI then computes their Zernike moments and returns
     * the Euclidean distance between them.
     *
     * @param iamgeURI0 a URI of an image to be compared
     * @param iamgeURI1 a URI of an image to be compared
     * @param degree  the degree to which to compute the Zernike moments to
     * @param center  the point at which to take Zernike moments from
     * @param radius  the radius of the circle to use
     * @return the Euclidean distance between the Zernike moments of the two images
     */
    public static double compare(String iamgeURI0, String iamgeURI1, int degree, Point2D center, double radius) throws IOException {
        BufferedImage image0 = ImageIO.read(new File(iamgeURI0));
        BufferedImage image1 = ImageIO.read(new File(iamgeURI1));

        return compare(image0, image1, degree, center, radius);
    }
}
