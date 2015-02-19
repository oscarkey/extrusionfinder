package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Part
 *
 * @author as2388
 */
public class PartTester {

    /**
     * Tests the equals method of part
     */
    @Test
    public void testEquals() {
        Part a = new Part("a", "b", "c", "d");

        assertTrue(new Part("a", "b", "c", "d").equals(a));

        assertTrue(!new Part("A", "b", "c", "d").equals(a));
        assertTrue(!new Part("b", "B", "c", "d").equals(a));
        assertTrue(!new Part("b", "b", "C", "d").equals(a));
        assertTrue(!new Part("b", "b", "c", "D").equals(a));

        assertTrue(!a.equals(new Object()));
    }
}
