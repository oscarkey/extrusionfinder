package uk.ac.cam.echo.extrusionfinder.zernike;

class Complex {

	public final double real;
	public final double imag;
	
	Complex(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}
	
	public Complex conjugate() {
		return new Complex(real, -1*imag);
	}
	
	public Complex add(Complex z) {
		return new Complex(real+z.real, imag+z.imag);
	}
	public Complex multiply(double x) {
		return new Complex(real*x, imag*x);
	}
	
	public double modulus() {
		return Math.hypot(real, imag);
	}
	
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