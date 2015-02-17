package uk.ac.cam.echo.extrusionfinder.zernike;

public class ZernikePoint {

    //A vector of the position of this point raised to a power of the index into the vector
    //e.g posToPower[i] = position^i
	private final Complex[] positionToPower;
    //The modulus of point
	public final double modulus;
    //The grayscale color (between 0 and 255) of the point
	private double intensity;

    /**
     * Gets the position of the point raised to a power of exponent. This is achieved
     * by looking up the value in a table of precomputed powers of the position.
     * @param exponent the power to raise the position to
     * @return the position of the point raised to a power of the exponent
     */
	public Complex getPosition(int exponent) {
        assert 0 <= exponent && exponent < positionToPower.length;
        return positionToPower[exponent];
    }

    /**
     * Gets the grayscale color (between 0 and 255) of the point.
     * @return the grayscale color (between 0 and 255) of the point.
     */
	public double getIntensity() {
		return intensity;
	}

    /**
     * Constructs a new ZernikePoint with the specified position and color intensity. The
     * position is raised to each power between 0 and maxPower to save computing it every
     * time it needed.
     * @param position the position of the ZernikePoint
     * @param intensity the grayscale color (between 0 and 255) of the point
     * @param maxPower
     */
	ZernikePoint(Complex position, double intensity, int maxPower) {
        this.positionToPower = new Complex[maxPower];
        for (int i=0; i<maxPower; i++) positionToPower[i] = position.pow(i);
		this.modulus = position.modulus();
		this.intensity = intensity;
	}

    /**
     * Normalizes the intensity such that the sum of intensities of all points equals 1.
     * @param n the total intensity of the image
     */
	public void normalizeIntensity(double n) {
		intensity /= n;
	}
}