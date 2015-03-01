package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.util.FileUtility;

import java.net.UnknownHostException;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Integration test for the part sourcer.
 * Runs the whole thing with database etc.
 * Slow, due to 3 sleeps being hardcoded into crawler4j. So only use to test,
 * not usually for just building the project.
 */

/* The PowerMockIgnore below apparently prevents some weird things from failing
 * in the mock class loader. For more (but not much more) information, see:
 * https://code.google.com/p/powermock/issues/detail?id=277
 */
@PowerMockIgnore({"javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Configuration.class} )
public class SourcerIntegrationTester {

    private static final String TEMP_FOLDER = "crawlerdata/crawlertest";
    private static final String DB_NAME = "test";
    private MongoDBManager dbManager;

    @Before
    public void setUp() throws UnknownHostException {
        dbManager = new MongoDBManager(DB_NAME);
        dbManager.clearDatabase();
        FileUtility.makeDir(TEMP_FOLDER);
    }

    /**
     * Runs the whole sourcer but fetches a limited amount of pages.
     * If it fails, it's probably due to the data being wrong (since it is
     * totally unrobust against changes on the manufacturer's websites).
     * Functionality of all other components should be tested in the other
     * (unit) tests.
     * Currently only testing results of Seagate.
     */
    @Test
    public void testPartSourcer() {

        mockStatic(Configuration.class);

        expect(Configuration.getCrawlStorageFolder()).andReturn(TEMP_FOLDER);
        expect(Configuration.getCrawlStorageFolder()).andReturn(TEMP_FOLDER);
        expect(Configuration.getMaxCrawlDepth()).andReturn(-1);
        expect(Configuration.getMaxCrawlPages()).andReturn(10);

        replayAll();

        PartSourcer.main(new String[] { DB_NAME });
        try {
            Part p1= dbManager.loadPart("SG000SG2002");
            Part p2= dbManager.loadPart("SG000SG1776");
            Part p3= dbManager.loadPart("SG000SG1316");
            Part p4= dbManager.loadPart("SG000SG1697");
            Part p5= dbManager.loadPart("SG000SG1523");

            Size s1 = new Size(1.0f, 1.0f, Unit.IN);
            Size s2 = new Size(8.0f, 3.0f, Unit.IN);
            Size s3 = new Size(2.0f, Unit.MM);
            Size s4 = new Size();
            Size s5 = new Size(2.0f, Unit.IN);

            String pdf1 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg2002 pdf (1).pdf";
            String pdf2 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1776 pdf (1).pdf";
            String pdf3 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1316 pdf (1).pdf";
            String pdf4 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1697 pdf (1).pdf";
            String pdf5 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1523 pdf (1).pdf";

            assertEquals(s1, p1.getSize());
            assertEquals(s2, p2.getSize());
            assertEquals(s3, p3.getSize());
            assertEquals(s4, p4.getSize());
            assertEquals(s5, p5.getSize());

            assertEquals(pdf1, p1.getLink());
            assertEquals(pdf2, p2.getLink());
            assertEquals(pdf3, p3.getLink());
            assertEquals(pdf4, p4.getLink());
            assertEquals(pdf5, p5.getLink());

            // test the zernike moments!

            Map<String, double[]> zm = dbManager.loadZernikeMap().getZernikeMap();
            assertNotNull(zm.get(p1.get_id()));
            assertNotNull(zm.get(p2.get_id()));
            assertNotNull(zm.get(p3.get_id()));
            assertNotNull(zm.get(p4.get_id()));
            assertNotNull(zm.get(p5.get_id()));

        } catch (ItemNotFoundException e) {
            fail(e.getMessage());
        }

        verifyAll();

    }

    @After
    public void tearDown() {
        dbManager.clearDatabase();
        FileUtility.delete(TEMP_FOLDER);
    }
}

