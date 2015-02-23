package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import java.util.Collection;
import java.io.File;
import java.net.UnknownHostException;
import java.lang.Thread;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Integration test for the part sourcer.
 * Runs the whole thing with database etc.
 * Slow, due to 3 sleeps being hardcoded into crawler4j. So only use to test,
 * not usually for just building the project.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Configuration.class} )
@PowerMockIgnore( {"javax.management.*"}) // this prevents some weird exception ...
public class SourcerIntegrationTester {

    private static final String TEMP_FOLDER = "crawlerdata/crawlertest";
    private static final String DB_NAME = "test";
    private MongoDBManager dbManager;
    private Collection crawlers;
    private File folder;

    @Before
    public void setUp() throws UnknownHostException {
        dbManager = new MongoDBManager(DB_NAME);
        dbManager.clearDatabase();
        folder = new File(TEMP_FOLDER);
        folder.mkdir();
        crawlers = Configuration.getCrawlers();
    }

    /**
     * Runs the whole sourcer but fetches a limited amount of pages.
     * If it fails, it's probably due to the data being wrong (since it is
     * totally unrobust against changes on the manufacturer's websites).
     * Functionality of all other components should be tested in the other
     * (unit) tests.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPartSourcer() throws Exception {

        mockStatic(Configuration.class);

        expect(Configuration.getCrawlStorageFolder()).andReturn(TEMP_FOLDER);
        expect(Configuration.getMaxCrawlDepth()).andReturn(-1);
        expect(Configuration.getMaxCrawlPages()).andReturn(20);
        expect(Configuration.getCrawlers()).andReturn(crawlers);

        replayAll();

        PartSourcer.main(new String[] { DB_NAME });
        try {
            Part p1= dbManager.loadPart("1SG1551");
            Part p2= dbManager.loadPart("1SG1222");
            Part p3= dbManager.loadPart("1SG1678");
            Part p4= dbManager.loadPart("1SG2511");
            Part p5= dbManager.loadPart("1SG1832");

            Size s1 = new Size(2.340f, Unit.IN);
            Size s2 = new Size();
            Size s3 = new Size(0.120f, Unit.IN);
            Size s4 = new Size(1.880f, 1.500f, Unit.IN );
            Size s5 = new Size(3f, Unit.MM);

            String pdf1 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1551 pdf (1).pdf";
            String pdf2 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1222 pdf (1).pdf";
            String pdf3 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1678 pdf (1).pdf";
            String pdf4 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg2511 pdf (1).pdf";
            String pdf5 = "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1832 pdf (1).pdf";

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

        } catch (ItemNotFoundException e) {
            fail(e.getMessage());
        }

        verifyAll();

    }

    @After
    public void tearDown() {
        dbManager.clearDatabase();
        delete(folder);
    }

    // delete a directory: http://stackoverflow.com/a/3775718
    private static void delete(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {

                    if(files[i].isDirectory()) {
                        delete(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        path.delete();
    }
}

