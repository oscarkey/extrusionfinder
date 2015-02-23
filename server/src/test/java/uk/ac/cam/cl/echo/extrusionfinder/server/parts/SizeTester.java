package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Unit tests for Size.
 */
public class SizeTester {

    @Test
    public void testEmptySize() {
        Size s = new Size();
        assertTrue(s.getDimension1() == 0);
        assertTrue(s.getDimension2() == 0);
        assertTrue(s.getUnit().equals(Unit.UNKNOWN));
    }

    @Test
    public void testOneDimSize() {
        Size s = new Size(3.14f, Unit.MM);
        assertTrue(s.getDimension1() == 3.14f);
        assertTrue(s.getDimension2() == 0);
        assertTrue(s.getUnit().equals(Unit.MM));
    }

    @Test
    public void testTwoDimSize() {
        Size s = new Size(3.14f, 0.211f, Unit.IN);
        assertTrue(s.getDimension1() == 3.14f);
        assertTrue(s.getDimension2() == 0.211f);
        assertTrue(s.getUnit().equals(Unit.IN));
    }

    @Test
    public void testNegativeSize() {
        Size s = new Size(-3.14f, -0.211f, Unit.IN);
        assertTrue(s.getDimension1() == 0);
        assertTrue(s.getDimension2() == 0);
        assertTrue(s.getUnit().equals(Unit.IN));
    }

    @Test
    public void testEquals() {
        Size s1 = new Size();
        Size s2 = new Size(0, Unit.UNKNOWN);
        Size s3 = new Size(1.0f, 0.1f, Unit.MM);
        Size s4 = new Size(1.0f, 0.1f, Unit.MM);
        Size s5 = new Size(0.1f, 1.0f, Unit.MM);
        Size s6 = new Size(1.0f, 0.1f, Unit.IN);

        assertTrue(s1.equals(s2));
        assertTrue(s3.equals(s3));
        assertFalse(s3.equals(s5));
        assertFalse(s3.equals(s6));
        assertFalse(s1.equals(new Object()));
    }
}
