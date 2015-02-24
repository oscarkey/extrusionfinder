package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.SeagateCrawler;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for Manufacturer.
 */
public class ManufacturerTester {

    @Test
    public void testEquals() {

        Manufacturer m1 = new Manufacturer("id1", "m1", "info", "link", new String[] { "seeds" }, new SeagateCrawler());
        Manufacturer m2 = new Manufacturer("id1", "m1", "info", "link", new String[] { "seeds" }, new SeagateCrawler());
        Manufacturer m3 = new Manufacturer("id2", "m1", "info", "link", new String[] { "seeds" }, new SeagateCrawler());
        Manufacturer m4 = new Manufacturer("id1", "m2", "info", "link", new String[] { "seeds" }, new SeagateCrawler());
        Manufacturer m5 = new Manufacturer("id1", "m1", "infi", "link", new String[] { "seeds" }, new SeagateCrawler());
        Manufacturer m6 = new Manufacturer("id1", "m1", "info", "lank", new String[] { "seeds" }, new SeagateCrawler());
        Manufacturer m7 = new Manufacturer("id1", "m1", "info", "link", new String[] { }, new SeagateCrawler());
        Manufacturer m8 = new Manufacturer("id1", "m1", "info", "link", new String[] { "seeds" }, null);
        // we don't have other crawlers than SeagateCrawler to test for equality!!

        assertTrue(m1.equals(m2));
        assertFalse(m1.equals(m3));
        assertFalse(m1.equals(m4));
        assertFalse(m1.equals(m5));
        assertFalse(m1.equals(m6));
        assertFalse(m1.equals(m7));
        assertFalse(m1.equals(m8));
        assertFalse(m1.equals(new Object()));
    }
}
