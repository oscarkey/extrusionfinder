package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
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

/*
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Configuration.class, Thread.class} )
@PowerMockIgnore( {"javax.management.*"}) 
public class SourcerTester {

    private static final String TEMP_FOLDER = "crawlertest";
    private static final String DB_NAME = "test";
    private MongoDBManager dbManager;
    private Collection crawlers;

    @Before
    public void setUp() throws UnknownHostException {
        dbManager = new MongoDBManager(DB_NAME);
        dbManager.clearDatabase();
        File dir = new File(TEMP_FOLDER);
        dir.mkdir();
        crawlers = Configuration.getCrawlers();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPartSourcer() throws Exception {

        mockStatic(Configuration.class);
        mockStatic(Thread.class);

        expect(Configuration.getCrawlStorageFolder()).andReturn(TEMP_FOLDER);
        expect(Configuration.getMaxCrawlDepth()).andReturn(-1);
        expect(Configuration.getMaxCrawlPages()).andReturn(20);
        expect(Configuration.getCrawlers()).andReturn(crawlers);

        suppress(method(Thread.class, "sleep", long.class));


        replayAll();

        PartSourcer.main(new String[] { DB_NAME });
        try {
            dbManager.loadPart("1SG1551");
            dbManager.loadPart("1SG1222");
            dbManager.loadPart("1SG1678");
            dbManager.loadPart("1SG1971");
            dbManager.loadPart("1SG1832");

        } catch (ItemNotFoundException e) {
            fail(e.getMessage());
        }

        verifyAll();

    }

    @After
    public void tearDown() {
        dbManager.clearDatabase();
        File path = new File(TEMP_FOLDER);
        deleteDirectory(path);
    }


    // delete a directory: http://stackoverflow.com/a/3775718
    private void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {

                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        path.delete();
    }

}
*/
