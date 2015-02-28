package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for MatchedPart
 *
 * @author as2388
 */
public class MatchedPartTester {

    /**
     * Tests the equals method of MatchedPart
     */
    @Test
    public void testEquals() {
        MatchedPart a = new MatchedPart(new Part("a", "b", "c", "d", "e"), 0.02);

        assertTrue(new MatchedPart(new Part("a", "b", "c", "d", "e"), 0.02).equals(a));

        assertTrue(!new MatchedPart(new Part("A", "b", "c", "d", "e"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("a", "B", "c", "d", "e"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("a", "b", "C", "d", "e"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("a", "b", "c", "D", "e"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("a", "b", "c", "d", "E"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("a", "b", "c", "d", "e"), 0.03).equals(a));

        assertTrue(!a.equals(new Object()));
    }
}
