package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Part
 *
 * @author as2388
 */
public class PartTester {

    /**
     * Tests the equals method of part
     * Size and description are not included in equality test.
     */
    @Test
    public void testEquals() {
        Part a = new Part("a", "b", "c", "d", new Size(), "desc");

        assertTrue(new Part("a", "b", "c", "d", new Size(), "desc").equals(a));

        assertTrue(!new Part("a", "b", "c", "d").equals(a));
        assertTrue(!new Part("A", "b", "c", "d", new Size(), "desc").equals(a));
        assertTrue(!new Part("b", "B", "c", "d", new Size(), "desc").equals(a));
        assertTrue(!new Part("b", "b", "C", "d", new Size(), "desc").equals(a));
        assertTrue(!new Part("b", "b", "c", "D", new Size(), "desc").equals(a));
        assertTrue(!new Part("a", "b", "c", "d", new Size(1.0f, 2.0f, Unit.IN), "desc").equals(a));
        assertTrue(!new Part("a", "b", "c", "d", new Size(), "bla").equals(a));

        assertTrue(!a.equals(new Object()));
    }
}
