package uk.ac.cam.echo.extrusionfinder.zernike;

class Complex {

    //The real component of the complex number.
	public final double real;
    //The imaginary component of the complex number.
	public final double imag;

    /**
     * Constructs a new Complex number with the given real and imaginary components.
     * @param real the real component of the complex number
     * @param imag the imaginary component of the complex number
     */
	Complex(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}

    /**
     * Computes the complex conjugate of the complex number and returns this
     * as a new Complex object.
     * @return a new Complex object with the same real component and negated imaginary component
     */
	public Complex conjugate() {
		return new Complex(real, -1*imag);
	}

    /**
     * Adds the complex number z to this and returns the result as a new Complex object.
     * @param z the complex number to be added
     * @return a new Complex object that is the sum of this and z
     */
	public Complex add(Complex z) {
		return new Complex(real+z.real, imag+z.imag);
	}
    /**
     * Multiplies this by a real number and returns the result as a new Complex object.
     * @param x the real number to multiply by
     * @return a new Complex object that is the product of this and x
     */
	public Complex multiply(double x) {
		return new Complex(real*x, imag*x);
	}

    /**
     * Computes the modulus of this.
     * @return the modulus of this
     */
	public double modulus() {
		return Math.hypot(real, imag);
	}

    /**
     * Raises this to the power of a natural number and returns the result as a new
     * Complex object.
     * @param exponent the natural number to raise this to the power of
     * @return a new Complex object that is this raised to a power of exponent
     */
	public Complex pow(int exponent) {
		double real = (double)1.0;
		double imag = (double)0.0;
		for (int i=exponent; i>0; i--) {
			double t = real;
			real = real*this.real - imag*this.imag;
			imag = t*this.imag + imag*this.real;
		}
		return new Complex(real, imag);
	}
}