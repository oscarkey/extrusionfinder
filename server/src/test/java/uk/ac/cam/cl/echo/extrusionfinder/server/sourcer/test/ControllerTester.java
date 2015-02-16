package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.test;

import org.junit.Test;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

import java.lang.IllegalArgumentException;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.Controller;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.ExtendedCrawler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the crawler controller.
 */

public class ControllerTester {

    /**
     * Tests that all required methods are called when controller is run.
     */
    @Test
    public void testControlledCrawl() throws Exception {

        CrawlController crawlController = mock(CrawlController.class);
        ExtendedCrawler crawler = getDummyCrawler(new String[] { "foo" });
        Controller controller =
            new Controller<ExtendedCrawler>(crawlController, crawler);

        // test running the controller - invoking the right methods
        Stream st = controller.start();
        verify(crawlController, atLeastOnce()).addSeed(any(String.class));
        verify(crawlController).start(any(), anyInt());

        controller.stop();
        verify(crawlController).shutdown();
        verify(crawlController).waitUntilFinish();

        assertNotNull(st);
    }

    /**
     * Tests that constructor fails when passed crawler with no seeds.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCrawler() {

        CrawlController crawlController = mock(CrawlController.class);
        ExtendedCrawler crawler = getDummyCrawler(new String[] {});
        Controller controller =
            new Controller<ExtendedCrawler>(crawlController, crawler);
    }

    /**
     * @param seeds The URL seeds for the vendor that this crawler represents.
     * @return      Instance of ExtendedCrawler.
     */
    private ExtendedCrawler getDummyCrawler(String[] seeds) {

        ExtendedCrawler crawler = new ExtendedCrawler() {

            @Override
            public void configure(Stream.Builder<Part> prts) {}

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
}
