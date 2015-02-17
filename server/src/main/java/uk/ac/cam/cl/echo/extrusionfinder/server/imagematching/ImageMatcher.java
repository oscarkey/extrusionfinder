package uk.ac.cam.cl.echo.extrusionfinder.server.imagematching;

import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.Zernike;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class ImageMatcher {

    //The Zernike moments of the ImageMatcher
    private double[] zm;

    /**
     * Constructs an ImageMatcher with the Zernike moments computed using the img,
     * degree, center and radius parameters.
     * @param img the image to compute Zernike moments for
     * @param degree the degree to which to compute the Zernike moments to
     * @param center the point at which to take Zernike moments from
     * @param radius the radius of the circle to use
     */
    ImageMatcher(BufferedImage img, int degree, Point2D center, double radius) {
        zm = Zernike.zernikeMoments(img, degree, center, radius);
    }

    /**
     * Computes the Euclidean distance between an n-dimensional vector and this.zm.
     * @param zm the Zernike moments to be compared with
     * @return the Euclidean distance between this.zm and zm
     */
    public double compare(double[] zm) {
        return distance(this.zm, zm);
    }

    /**
     * Computes the Euclidean distance between 2 vectors. If one of the vectors has
     * more dimensions than the other, they are ignored.
     * @param A an n-dimensional vector
     * @param B an m-dimensional vector
     * @return the Euclidean distance between the two vectors.
     */
	private static double distance(double[] A, double[] B) {
		double x = 0;
		for (int i=Math.min(A.length, B.length)-1; i>=0; i--) {
			x += Math.pow(Math.abs(A[i]) - Math.abs(B[i]), 2);
		}
		return Math.sqrt(x);
	}

    /**
     * Computes the Zernike moments of the two images and returns the Euclidean
     * distance between them.
     * @param img0 an image to be compared
     * @param img1 an image to be compared
     * @param degree the degree to which to compute the Zernike moments to
     * @param center the point at which to take Zernike moments from
     * @param radius the radius of the circle to use
     * @return the Euclidean distance between the Zernike moments of the two images
     */
	public static double compare(BufferedImage img0, BufferedImage img1, int degree, Point2D center, double radius) {
		double[] zm0 = Zernike.zernikeMoments(img0, degree, center, radius);
		double[] zm1 = Zernike.zernikeMoments(img1, degree, center, radius);
		
		return distance(zm0, zm1);
	}
    /**
     * Reads the images from their URI then computes their Zernike moments and returns
     * the Euclidean distance between them.
     * @param imgURI0 a URI of an image to be compared
     * @param imgURI1 a URI of an image to be compared
     * @param degree the degree to which to compute the Zernike moments to
     * @param center the point at which to take Zernike moments from
     * @param radius the radius of the circle to use
     * @return the Euclidean distance between the Zernike moments of the two images
     */
	public static double compare(String imgURI0, String imgURI1, int degree, Point2D center, double radius) throws IOException {
		BufferedImage img0 = null;
		BufferedImage img1 = null;

        img0 = ImageIO.read(new File(imgURI0));
		img1 = ImageIO.read(new File(imgURI1));
		
		return compare(img0, img1, degree, center, radius);
	}
}



