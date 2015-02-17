package uk.ac.cam.cl.echo.extrusionfinder.server.zernike;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Zernike {

    /**
     * Computes the sum n*(n-1)*...2*1
     * @param n a number
     * @return n!
     */
	private static double factorial(int n) {
		double x = 1;
		for (int i=n; i>0; i--) x *= i;
		return x;
	}

    /**
     * Computes a Zernike moment
     * @param zps the points to use to compute the Zernike moment
     * @param zpsSize the number of points in zps
     * @param n a value
     * @param m the exponent to raise the position of each ZernikePoint to
     * @return a Zernike moment
     */
	private static Complex zernikeMoment(ZernikePoint[] zps, int zpsSize, int n, int m) {
		int p = (n-m)/2;
		int q = (n+m)/2;
		Complex v = new Complex(0, 0);
		double[] gm = new double[p+1];
		
        for (int i=0; i<=p; i+=2) gm[i] = factorial(n-i) / (factorial(i) * factorial(q-i) * factorial(p-i));
		for (int i=1; i<=p; i+=2) gm[i] = -1 * factorial(n-i) / (factorial(i) * factorial(q-i) * factorial(p-i));
		
		for (int i=0; i<zpsSize; i++) {
			double r = zps[i].modulus;
			Complex z = zps[i].getPosition(m);
			double c = zps[i].getIntensity();
			Complex Vnl = new Complex(0, 0);
			
			for (int j=0; j<=p; j++) {
				Vnl = Vnl.add(z.multiply(gm[j] * Math.pow(r, n-2*j)));
			}
			v = v.add(Vnl.conjugate().multiply(c));
		}
		
		v = v.multiply((n+1)/Math.PI);
		return v;
	}

    /**
     * Computes Zernike moments of an image to a particular degree using a circle
     * with the specified radius and center.
     * @param img the image to use for computing Zernike oments
     * @param imgWidth the width of img in pixels
     * @param degree the degree to which to compute the Zernike moments to
     * @param center the point at which to take Zernike moments from
     * @param radius the radius of the circle to use
     * @return a vector of the magitudes of each Zernike moment
     */
	public static double[] zernikeMoments(byte[] img, int imgWidth, int degree, Point2D center, double radius) {
		int imgHeight = img.length / imgWidth;
		int zpsSize = (int) (Math.PI*radius*radius);
		ZernikePoint[] zps = new ZernikePoint[zpsSize];
		int totalIntensity = 0;
		
		int i=0;
		for (int y0=0; y0<imgHeight; y0++) {
			double y = (y0-center.getY())/radius;
			for (int x0=0; x0<imgWidth; x0++) {
				double x = (x0-center.getX())/radius;
				double r = Math.hypot(x, y);
				if (r <= 1) {
					int c = img[x0+y0*imgWidth] & 0xFF;
					if (c > 0) {
						r = Math.max(r, 1e-9);
						Complex z = new Complex(x/r, y/r);
						zps[i] = new ZernikePoint(z, c, degree);
						totalIntensity += c;
						i++;
					}
				}
			}
		}
		zpsSize = i;
		for (i=0; i<zpsSize; i++) zps[i].normalizeIntensity(totalIntensity);

		double[] zvalues = new double[(int)(2*Math.pow(degree,2)+4*degree-Math.pow(-1,degree)+1)/8];
		i=0;
		for (int n=0; n<degree; n++) {
            for (int m = 0; m <= n; m++) {
                if ((n - m) % 2 == 0) {
                    Complex z = zernikeMoment(zps, zpsSize, n, m);
                    zvalues[i] = z.modulus();
                    i++;
                }
            }
        }
		return zvalues;
	}
    /**
     * Computes Zernike moments of an image to a particular degree using a circle
     * with the specified radius and center. The image is converted to grayscale
     * format if it is not already.
     * @param img the image to use for computing Zernike oments
     * @param degree the degree to which to compute the Zernike moments to
     * @param center the point at which to take Zernike moments from
     * @param radius the radius of the circle to use
     * @return a vector of the magitudes of each Zernike moment
     */
	public static double[] zernikeMoments(BufferedImage img, int degree, Point2D center, double radius) {
		if (img.getType() != BufferedImage.TYPE_BYTE_GRAY) {
			BufferedImage t = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			t.getGraphics().drawImage(img, 0, 0, null);
			img = t;
		}
		byte[] imgBytes = ((DataBufferByte) img.getData().getDataBuffer()).getData();
		return zernikeMoments(imgBytes, img.getWidth(), degree, center, radius);
	}
}