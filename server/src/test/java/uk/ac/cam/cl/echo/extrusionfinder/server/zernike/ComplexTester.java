package uk.ac.cam.cl.echo.extrusionfinder.server.zernike;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ComplexTester {

    @Test
    public void testConstructor() {
        Complex z1 = new Complex(-5, 10);
        Complex z2 = new Complex(4, -12);
        assertTrue(z1.real == -5 && z1.imag == 10);
        assertTrue(z2.real == 4 && z2.imag == -12);
    }

    @Test
    public void testConjugate() {
        Complex z1 = new Complex(10, 5);
        Complex z2 = z1.conjugate();
        assertTrue(z2.real == 10 && z2.imag == -5);
    }

    @Test
    public void testAdd() {
        Complex z1 = new Complex(10, 5);
        Complex z2 = new Complex(3, 14);
        Complex z3 = z1.add(z2);
        assertTrue(z3.real == 13 && z3.imag == 19);
    }

    @Test
    public void testMultiply() {
        Complex z1 = new Complex(9, -8);
        Complex z2 = new Complex(3, -6);
        Complex z3 = z1.multiply(4);
        Complex z4 = z1.multiply(z2);
        assertTrue(z3.real == 36 && z3.imag == -32);
        assertTrue(z4.real == -21 && z4.imag == -78);
    }

    @Test
    public void testModulus() {
        Complex z1 = new Complex(12, 5);
        double r = z1.modulus();
        assertTrue(r == 13.0);
    }
}