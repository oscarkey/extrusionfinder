package uk.ac.cam.echo.extrusionfinder.server.zernike;

import java.awt.geom.Point2D;
import java.util.Arrays;

public class ZernikeOld {

	private static final int MAX_COLOR_DEPTH = 1;

	private static Point2D centerOfMass(int[][] im) {
		int sumX, sumY, i;
		sumX = sumY = i = 0;
		for (int y=0; y<im.length; y++) {
			for (int x=0; x<im[0].length; x++) {
				sumX += im[y][x]*x;
				sumY += im[y][x]*y;
				i += im[y][x];
			}
		}
		return new Point2D.Double(sumX/i/MAX_COLOR_DEPTH, sumY/i/MAX_COLOR_DEPTH);
	}
	private static int[][][] mgrid(int size) {
		int[][][] A = new int[2][size][size];
		for (int y=0; y<size; y++) {
			for (int x=0; x<size; x++) {
				A[0][y][x] = y;
				A[1][y][x] = x;
			}
		}
		return A;
	}
	private static double[] ravel(double[][] X) {
		double[] A = new double[X.length*X[0].length];
		for (int y=0; y<X.length; y++) {
			for (int x=0; x<X[0].length; x++) {
				A[x+X[0].length*y] = X[y][x];
			}
		}
		return A;
	}
	private static int[] ravel(int[][] X) {
		int[] A = new int[X.length*X[0].length];
		for (int y=0; y<X.length; y++) {
			for (int x=0; x<X[0].length; x++) {
				A[x+X[0].length*y] = X[y][x];
			}
		}
		return A;
	}
	private static double[] rescale(int[][] X, double offset, double radius) {
		double[][] A = new double[X.length][X[0].length];
		for (int y=0; y<X.length; y++) {
			for (int x=0; x<X[0].length; x++) {
				A[y][x] = (double) X[y][x];
				A[y][x] -= offset;
				A[y][x] /= radius;
			}
		}
		return ravel(A);
	}
	private static double[] hypot(double[] X, double[] Y) {
		double[] A = new double[X.length];
		for (int i=0; i<X.length; i++) {
			A[i] = Math.hypot(X[i], Y[i]);
		}
		return A;
	}
	private static void maximum(double[] X, double y) {
		for (int i=0; i<X.length; i++) {
			X[i] = Math.max(X[i], y);
		}
	}
	private static int[] circularize(int[] P, double[] mask) {
		int[] A = new int[P.length];
		for (int i=0; i<P.length; i++) {
			if (mask[i]<=1) A[i] = P[i];
			else A[i] = 0;
		}
		return A;
	}
	private static double[] applyMask(int[] target, int[] mask) {
		int maskSize = 0;
		for (int i=0; i<mask.length; i++) if (mask[i]==1) maskSize++;
		
		double[] A = new double[maskSize];
		for (int i=0, j=0; i<mask.length; i++) {
			if (mask[i]==1&&target[i]>0) {
				A[j] = target[i];
				j++;
			}
		}
		return A;
	}
	private static double[] applyMask(double[] target, int[] mask) {
		int maskSize = 0;
		for (int i=0; i<mask.length; i++) if (mask[i]==1) maskSize++;
		
		double[] A = new double[maskSize];
		for (int i=0, j=0; i<mask.length; i++) {
			if (mask[i]==1) {
				A[j] = target[i];
				j++;
			}
		}
		return A;
	}
	private static double sum(double[] A) {
		double total = 0;
		for (int i=0; i<A.length; i++) {
			total += A[i];
		}
		return total;
	}
	private static double[] divideAll(double[] dividends, double divisor) {
		double[] A = new double[dividends.length];
		for (int i=0; i<dividends.length; i++) {
			A[i] = dividends[i] / divisor;
		}
		return A;
	}
	private static double[] divideAll(double[] dividends, double[] divisors) {
		double[] A = new double[dividends.length];
		for (int i=0; i<dividends.length; i++) {
			A[i] = dividends[i] / divisors[i];
		}
		return A;
	}
	private static Complex[] createComplex(double[] X, double[] Y, double[] D) {
		Complex[] A = new Complex[Y.length];
		for (int i=0; i<A.length; i++) {
			A[i] = new Complex(X[i]/D[i], Y[i]/D[i]);
		}
		return A;
	}
	private static Complex[] pow(Complex[] bases, int exponent) {
		Complex[] A = new Complex[bases.length];
		for (int i=0; i<bases.length; i++) {
			A[i] = bases[i].pow(exponent);
		}
		return A;
	}
	private static double fact(int n) {
		double f = 1;
		for (int i=n; i>0; i--) f *= i;
		return f;
	}
	
	private static Complex zenrikeMoment(double[] Da, Complex[] Aa, double[] Pa, int n, int l) {
		double[] D = Da;
		Complex[] A = Aa;
		double[] P = Pa;
		final int Nelems = Da.length;
		Complex v = new Complex(0, 0);
		
		double[] g_m = new double[(n-l)/2+1];
		
        for (int m=0; m <= (n-l)/2; m++) {
			double f = (m%2!=0) ? -1 : 1;
			g_m[m] = f * fact(n-m) / (fact(m) * fact((n-2*m+l)/2) * fact((n-2*m-l)/2));
        }
		
		for (int i=0; i != Nelems; i++) {
			double d = D[i];
			Complex a = A[i];
			double p = P[i];
			Complex Vnl = new Complex(0, 0);
			for (int m=0; m <= (n-l)/2; m++) {
				Vnl = Vnl.add(a.multiply(g_m[m] * Math.pow(d, n - 2*m)));
			}
			v = v.add(Vnl.conjugate().multiply(p));
		}
		v = v.multiply((n+1)/Math.PI);
		return v;
	}

	/**
	 * Calcultes Zernike moments through degree 'degree'. These are computed
	 * on a circle of radius 'radius' centered around 'cm'. If a value for
	 * 'cm' is not supplied the center of mass of the image is used instead.
	 *
	 * @param im     input image
	 * @param radius maximum radius for the Zernike polynomials
	 * @param degree maximum degree of Zernike polynomials
	 * @param cm     center of mass of the image
	 *
	 * @return the Zenrike moments of the image 'im'
	 */
	public static double[] zernikeMoments(int[][] im, double radius, int degree, Point2D cm) {
		double[] zvalues = new double[(int)(2*Math.pow(degree+1,2)+4*(degree+1)-Math.pow(-1,degree+1)+1)/8];
		
		int[][] Y,X;
		{
			int[][][] A = mgrid(im.length);
			Y = A[0];
			X = A[1];
		}
		int[] P = ravel(im);
		
		double[] Yn = rescale(Y, cm.getY(), radius);
		double[] Xn = rescale(X, cm.getX(), radius);
		
		double[] Dn = hypot(Xn, Yn);
		maximum(Dn, 1e-9);
		int[] k = circularize(P, Dn);
		
		double[] frac_center = applyMask(P, k);
		frac_center = divideAll(frac_center, sum(frac_center));
		
		Yn = applyMask(Yn, k);
		Xn = applyMask(Xn, k);
		Dn = applyMask(Dn, k);
		
		Complex[] An = createComplex(Xn, Yn, Dn);
		
		Complex[][] Ans = new Complex[degree+2][];
		for (int p=0; p<degree+2; p++) Ans[p] = pow(An, p);
		
		int i=0;
		for (int n=0; n<=degree; n++) {
			for (int l=0; l<=n; l++) {
				if ((n-l)%2==0) {
					Complex z = zenrikeMoment(Dn, Ans[l], frac_center, n, l);
					zvalues[i] = z.modulus();
					i++;
				}
			}
		}
		
		return zvalues;
	}
	public static double[] zernikeMoments(int[][] im, double radius, int degree) {
		Point2D cm = centerOfMass(im);
		return zernikeMoments(im, radius, degree, cm);
	}
	public static double[] zernikeMoments(int[][] im, double radius, Point2D cm) {
		return zernikeMoments(im, radius, 8, cm);
	}
	public static double[] zernikeMoments(int[][] im, double radius) {
		Point2D cm = centerOfMass(im);
		return zernikeMoments(im, radius, 8, cm);
	}
}