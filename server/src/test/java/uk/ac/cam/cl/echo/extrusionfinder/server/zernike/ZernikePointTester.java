package uk.ac.cam.cl.echo.extrusionfinder.server.zernike;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ZernikePointTester {

    @Test
    public void testConstructor() {
        Complex position = new Complex(3, 4);
        double modulus = 5.0;
        double intensity = 50.0;
        int maxPower = 12;

        ZernikePoint zp = new ZernikePoint(position, modulus, intensity, maxPower);
        assertTrue(zp.modulus == modulus);
        assertTrue(zp.getIntensity() == intensity);
    }

    @Test
    public void testGetPosition() {
        Complex position = new Complex(3, 4);
        int maxPower = 12;
        ZernikePoint zp = new ZernikePoint(position, 5.0, 50.0, maxPower);

        Complex pos0 = new Complex(1, 0);
        Complex pos1 = new Complex(3, 4);
        Complex pos2 = new Complex(-7, 24);
        Complex pos3 = new Complex(-117, 44);
        Complex pos5 = new Complex(-237, -3116);
        Complex pos12 = new Complex(32125393, -242017776);

        assertTrue(zp.getPosition(0).real == pos0.real && zp.getPosition(0).imag == pos0.imag);
        assertTrue(zp.getPosition(1).real == pos1.real && zp.getPosition(1).imag == pos1.imag);
        assertTrue(zp.getPosition(2).real == pos2.real && zp.getPosition(2).imag == pos2.imag);
        assertTrue(zp.getPosition(3).real == pos3.real && zp.getPosition(3).imag == pos3.imag);
        assertTrue(zp.getPosition(5).real == pos5.real && zp.getPosition(5).imag == pos5.imag);
        assertTrue(zp.getPosition(12).real == pos12.real && zp.getPosition(12).imag == pos12.imag);

        try {
            zp.getPosition(-1);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException e) {
        }
        try {
            zp.getPosition(maxPower + 1);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testNormalizeIntensity() {
        Complex position = new Complex(3, 4);
        ZernikePoint zp = new ZernikePoint(position, 5.0, 50.0, 12);

        zp.normalizeIntensity(100);
        assertTrue(zp.getIntensity() == 0.5);
    }
}
