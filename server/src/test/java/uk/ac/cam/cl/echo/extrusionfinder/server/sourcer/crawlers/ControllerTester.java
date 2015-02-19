package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.lang.IllegalArgumentException;
import java.util.Collection;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the crawler controller.
 */

public class ControllerTester {

    private static final String TEMP_FOLDER = "crawlertest";

    @Before
    public void setUp() {
        File dir = new File(TEMP_FOLDER);
        dir.mkdir();
    }

    /**
     * Tests that all required methods are called when controller is run.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testControlledCrawl() throws CrawlerException {

        // instantiate with mocked crawlcontroller + dummy crawler
        CrawlController crawlController = mock(CrawlController.class);
        ExtendedCrawler crawler = getDummyCrawler(new String[] { "foo" });
        Controller controller =
            new Controller<ExtendedCrawler>(crawlController, crawler);

        // test running the controller - invoking the right methods
        Collection st = controller.start();
        verify(crawlController, atLeastOnce()).addSeed(any(String.class));

        // any argument of type Class<T>; need to do this in an unchecked way
        // because java-pre-8 does not have type inference
        final Class<? extends ExtendedCrawler> ctype = ExtendedCrawler.class;
        verify(crawlController).start(any(ctype.getClass()), anyInt());

        // test stopping the controller
        controller.stop();
        verify(crawlController).shutdown();
        verify(crawlController).waitUntilFinish();

        // thie final result of the crawl
        assertNotNull(st);
    }

    /* This just tests that constructing a controller with config options
     * doesn't throw an exception.
     * All other functionality is tested by other methods.
     */
    @Test
    public void testConstructor() throws CrawlerException {
        ExtendedCrawler crawler = getDummyCrawler(new String[] { "foo" });
        Controller controller =
            new Controller<ExtendedCrawler>(TEMP_FOLDER,-1,1, crawler);
    }

    /**
     * Tests that constructor fails when passed invalid CrawlController args,
     * e.g. storagefolder is empty string.
     */
    @Test(expected = CrawlerException.class)
    public void testInvalidController() throws CrawlerException {

        ExtendedCrawler crawler = getDummyCrawler(new String[] { "foo" });
        Controller controller =
            new Controller<ExtendedCrawler>("",-2,1, crawler);
    }

    /**
     * Tests that constructor fails when passed crawler with no seeds.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCrawler() throws CrawlerException {

        CrawlController crawlController = mock(CrawlController.class);
        ExtendedCrawler crawler = getDummyCrawler(new String[] {});
        Controller controller =
            new Controller<ExtendedCrawler>(crawlController, crawler);
    }

    @After
    public void tearDown() {
        File path = new File(TEMP_FOLDER);
        deleteDirectory(path);
    }

    /**
     * @param seeds The URL seeds for the vendor that this crawler represents.
     * @return      Instance of ExtendedCrawler.
     */
    private ExtendedCrawler getDummyCrawler(final String[] seeds) {

        ExtendedCrawler crawler = new ExtendedCrawler() {

            @Override
            public void configure(Collection<Part> prts) {}

            @Override
            public String[] getSeeds() { return seeds; }

            @Override
            public boolean shouldVisit(Page referringPage, WebURL url) {
                return true;
            }

            @Override
            public void visit(Page page) {}
        };

        return crawler;
    }

    /* delete a directory: http://stackoverflow.com/a/3775718 */
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
