package uk.ac.cam.cl.echo.extrusionfinder.server.zernike;

class ZernikePoint {

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
     *
     * @param exponent the power to raise the position to
     * @return the position of the point raised to a power of the exponent
     */
    public Complex getPosition(int exponent) throws IllegalArgumentException {
        if (exponent < 0 || positionToPower.length <= exponent) {
            throw new IllegalArgumentException("Exponent is not in correct range. Expected 0 <= exponent < maxExponent");
        }
        return positionToPower[exponent];
    }

    /**
     * Gets the grayscale color (between 0 and 255) of the point.
     *
     * @return the grayscale color (between 0 and 255) of the point.
     */
    public double getIntensity() {
        return intensity;
    }

    /**
     * Constructs a new ZernikePoint with the specified position and color intensity. The
     * position is raised to each power between 0 and maxPower to save computing it every
     * time it needed.
     *
     * @param position  the normalized position of the ZernikePoint
     * @param modulus   the modulus of the (unnormalized) position
     * @param intensity the grayscale color (between 0 and 255) of the point
     * @param maxPower
     */
    ZernikePoint(Complex position, double modulus, double intensity, int maxPower) {
        this.positionToPower = new Complex[maxPower+1];
        Complex power = new Complex(1.0, 0.0);
        for (int i = 0; i <= maxPower; i++) {
            positionToPower[i] = power;
            power = power.multiply(position);
        }
        this.modulus = modulus;
        this.intensity = intensity;
    }

    /**
     * Divides intensity by n.
     *
     * @param n the total intensity of the image
     */
    public void normalizeIntensity(double n) throws IllegalArgumentException {
        if (n == 0) {
            throw new IllegalArgumentException("Cannot divide by zero.");
        }
        intensity /= n;
    }
}
