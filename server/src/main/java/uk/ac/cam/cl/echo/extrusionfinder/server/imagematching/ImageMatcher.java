package uk.ac.cam.cl.echo.extrusionfinder.server.imagematching;

import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.Zernike;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class ImageMatcher {

	private static double correlation(double[] A, double[] B) {
		double x = 0;
		for (int i=0; i<A.length; i++) {
			x += Math.abs(Math.pow(A[i], 2) - Math.pow(B[i], 2));
		}
		return x;
	}

	public static double compare(BufferedImage img0, BufferedImage img1, int degree, Point2D center, double radius) {
		double[] zm0 = Zernike.zernikeMoments(img0, degree, center, radius);
		double[] zm1 = Zernike.zernikeMoments(img1, degree, center, radius);
		
		//System.out.println(Arrays.toString(zm0));
		//System.out.println(Arrays.toString(zm1));
		
		return correlation(zm0, zm1);
	}
	public static double compare(String imgURI0, String imgURI1, int degree, Point2D center, double radius) {
		BufferedImage img0 = null;
		BufferedImage img1 = null;
		try {
			img0 = ImageIO.read(new File(imgURI0));
			img1 = ImageIO.read(new File(imgURI1));
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		return compare(img0, img1, degree, center, radius);
	}

	public static void main(String[] args) {
		int degree = -1;
		Point2D center = null;
		double radius = -1;
		try {
			degree = Integer.parseInt(args[2]);
			center = new Point2D.Double(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
			radius = Double.parseDouble(args[5]);
		} catch (NumberFormatException e) {
			System.err.println(e.getMessage());
			System.exit(2);
		}
		
		assert degree >= 0;
		
		System.out.println(compare(args[0], args[1], degree, center, radius));
	}
}



