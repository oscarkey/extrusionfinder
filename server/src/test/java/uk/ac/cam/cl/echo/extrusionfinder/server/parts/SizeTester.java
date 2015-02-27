package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;

import java.lang.IllegalArgumentException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for Size.
 */
public class SizeTester {

    @Test
    public void testEmptySize() {
        Size s = new Size();
        assertNull(s.getDimension1());
        assertNull(s.getDimension2());
        assertTrue(s.getUnit().equals(Unit.UNKNOWN));
    }

    @Test
    public void testOneDimSize() {
        Size s = new Size(3.14f, Unit.MM);
        assertTrue(s.getDimension1() == 3.14f);
        assertNull(s.getDimension2());
        assertTrue(s.getUnit().equals(Unit.MM));
    }

    @Test
    public void testTwoDimSize() {
        Size s = new Size(3.14f, 0.211f, Unit.IN);
        assertTrue(s.getDimension1() == 3.14f);
        assertTrue(s.getDimension2() == 0.211f);
        assertTrue(s.getUnit().equals(Unit.IN));
    }

    /**
     * Tests that sizes initialised to a negative value throw exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeSize() {
        Size s = new Size(-3.14f, -0.211f, Unit.IN);
    }

    @Test
    public void testEquals() {
        Size s1 = new Size();
        Size s2 = new Size(0.0f, Unit.UNKNOWN);
        Size s3 = new Size(1.0f, 0.1f, Unit.MM);
        Size s4 = new Size(1.0f, 0.1f, Unit.MM);
        Size s5 = new Size(0.1f, 1.0f, Unit.MM);
        Size s6 = new Size(1.0f, 0.1f, Unit.IN);
        Size s7 = new Size(null, 0.0f, Unit.UNKNOWN);
        Size s8 = new Size(null, null, Unit.UNKNOWN);

        assertTrue(s3.equals(s4));
        assertFalse(s1.equals(s2));
        assertFalse(s3.equals(s5));
        assertFalse(s3.equals(s6));
        assertFalse(s1.equals(new Object()));
        assertTrue(s2.equals(s7));
        assertTrue(s1.equals(s8));

    }
}
