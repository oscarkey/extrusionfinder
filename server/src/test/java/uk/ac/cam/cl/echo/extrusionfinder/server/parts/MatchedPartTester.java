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
        MatchedPart a = new MatchedPart(new Part("a", "b", "c", "d"), 0.02);

        assertTrue(new MatchedPart(new Part("a", "b", "c", "d"), 0.02).equals(a));

        assertTrue(!new MatchedPart(new Part("A", "b", "c", "d"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("b", "B", "c", "d"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("b", "b", "C", "d"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("b", "b", "c", "D"), 0.02).equals(a));
        assertTrue(!new MatchedPart(new Part("b", "b", "c", "d"), 0.03).equals(a));

        assertTrue(!a.equals(new Object()));
    }
}
