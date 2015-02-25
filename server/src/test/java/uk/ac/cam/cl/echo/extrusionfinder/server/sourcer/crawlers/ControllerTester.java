package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.lang.IllegalArgumentException;
import java.util.List;
import java.util.Set;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests the crawler controller.
 */

public class ControllerTester {

    /**
     * Tests that all required methods are called when controller is run.
     * Suppresses unchecked warning due to mocking generic class (Class)
     * without typing its parameter.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testControlledCrawl() throws CrawlerException {

        // instantiate with mocked crawlcontroller + dummy crawler
        CrawlController crawlController = mock(CrawlController.class);
        ExtendedCrawler crawler = mock(ExtendedCrawler.class);
        String[] seeds = new String[] { "foo" };

        Controller controller =
            new Controller<ExtendedCrawler>(crawlController, crawler, seeds);

        // test running the controller - invoking the right methods
        Set st = controller.crawl();
        verify(crawlController, atLeastOnce()).addSeed(any(String.class));

        // any argument of type Class<T>; need to do this in an unchecked way
        // because java-pre-8 does not have type inference
        final Class<? extends ExtendedCrawler> ctype = ExtendedCrawler.class;
        verify(crawlController).start(any(ctype.getClass()), anyInt());

        // test stopping the controller
        controller.stop();
        verify(crawlController).shutdown();
        verify(crawlController).waitUntilFinish();

        // the final result of the crawl
        assertNotNull(st);
    }

    /**
     * Tests that constructor fails when passed empty seed array.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCrawler() throws CrawlerException {

        CrawlController crawlController = mock(CrawlController.class);
        ExtendedCrawler crawler = mock(ExtendedCrawler.class);
        String[] seeds = new String[] {};

        Controller controller =
            new Controller<ExtendedCrawler>(crawlController, crawler, seeds);
    }
}
