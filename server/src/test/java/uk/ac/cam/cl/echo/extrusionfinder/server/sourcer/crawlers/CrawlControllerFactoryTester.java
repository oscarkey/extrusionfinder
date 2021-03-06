package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.*;


/**
 * Unit tests the crawl controller factory.
 *
 * Ps. this uses PowerMock and not Mockito (unlike the rest of this package)
 * even though Mockito has something equivalent called PowerMockito.
 * The reason is that the PowerMockito gradle dependency apparently also
 * affects the regular Mockito which made some of the other tests in here
 * inexplicably fail, even though they don't use PowerMockito!
 * Yes, pretty lame.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(CrawlControllerFactory.class)
public class CrawlControllerFactoryTester {

    /**
     * <p>
     * Tests constructor for full intellij coverage.
     * Note that it doesn't check for privacy; this is because the PrepareForTest
     * annotation sets all private constructors to public for, well, testing purposes.
     * PrepareForTest is needed for the other tests.
     * </p>
     * Besides, unit testing is really for runtime testing, and instantiating
     * a CrawlControllerFactory elsewhere would create a compile-time error.
     */
    @Test
    public void testConstructor() throws Exception {

        Constructor constructor = CrawlControllerFactory.class.getDeclaredConstructor();
        Object c = constructor.newInstance();
        assertTrue(c instanceof CrawlControllerFactory);

    }

    /**
     * Tests that the CrawlController getter calls all the expected methods
     */
    @Test
    public void testGetSuccess() throws Exception {

		final String path = "directoryPath";
        final int maxDepth = 30;
        final int maxPages = 10;

        // expect construction of various crawler4j config stuff

		CrawlConfig config = createMockAndExpectNew(CrawlConfig.class);
        PageFetcher pagef = createMockAndExpectNew(PageFetcher.class, config);
        RobotstxtConfig robconf = createMockAndExpectNew(
            RobotstxtConfig.class);
        RobotstxtServer robserv = createMockAndExpectNew(
            RobotstxtServer.class, robconf, pagef);

        // expect setting the config parameters

        config.setCrawlStorageFolder(path);
        expectLastCall().once();

        config.setMaxDepthOfCrawling(maxDepth);
        expectLastCall().once();

        config.setMaxPagesToFetch(maxPages);
        expectLastCall().once();

        // expect creating the actual crawlcontroller using above parameters

        CrawlController cc = createMockAndExpectNew(
            CrawlController.class, config, pagef, robserv);

		replayAll();

        // call the getter
        CrawlController result =
            CrawlControllerFactory.get(path, maxDepth, maxPages);

        // check that we got the mocked crawlcontroller
        assertEquals(cc, result);

		verifyAll();
    }

    /**
     * Tests that factory properly encapsulates exception if thrown by the
     * CrawlController constructor.
     */
    @Test(expected = CrawlerException.class)
    public void testGetFailure() throws Exception {

		final String path = "directoryPath";
        final int maxDepth = 30;
        final int maxPages = 10;

        // do all the same things as in testGetSuccess so that EasyMock doesn't
        // complain ...

		CrawlConfig config = createMockAndExpectNew(CrawlConfig.class);
        config.setCrawlStorageFolder(path);
        expectLastCall().once();

        config.setMaxDepthOfCrawling(maxDepth);
        expectLastCall().once();

        config.setMaxPagesToFetch(maxPages);
        expectLastCall().once();

        PageFetcher pagef = createMockAndExpectNew(PageFetcher.class, config);
        RobotstxtConfig robconf = createMockAndExpectNew(
            RobotstxtConfig.class);
        RobotstxtServer robserv = createMockAndExpectNew(
            RobotstxtServer.class, robconf, pagef);

        // ... but this time, throw an exception.

        CrawlController cc = createMock(CrawlController.class);

        expectNew(CrawlController.class, config, pagef, robserv)
            .andThrow(new Exception("test"));

		replayAll();

        // call the getter, the exception should be encapsulated in a
        // CrawlerException, which is what we are testing.

        CrawlController result =
            CrawlControllerFactory.get(path, maxDepth, maxPages);
    }
}
