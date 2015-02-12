package uk.ac.cam.echo.extrusionfinder.server.zernike;

public class ZernikePoint {

	private final Complex[] posToPower;
	public final double modulus;
	private double intensity;
	
	public Complex getPosition(int exponent) {
		return posToPower[exponent];
	}
	public double getIntensity() {
		return intensity;
	}
	
	ZernikePoint(Complex position, double modulus, double intensity, int maxPower) {
		this.modulus = modulus;
		this.intensity = intensity;
		this.posToPower = new Complex[maxPower];
		
		for (int i=0; i<maxPower; i++) {
			posToPower[i] = position.pow(i);
		}
	}
	
	public void normalizeIntensity(double n) {
		intensity /= n;
	}
}