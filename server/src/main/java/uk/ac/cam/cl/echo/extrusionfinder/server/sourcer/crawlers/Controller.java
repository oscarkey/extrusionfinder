package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import edu.uci.ics.crawler4j.crawler.CrawlController;

import java.lang.IllegalArgumentException;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller for any web crawler. Manages configuration and parts stream.
 *
 * How it works using crawler4j:
 * crawler4j has the two classes CrawlController and WebCrawler.
 * CrawlController's start method is run with the class type of a class that
 * extends WebCrawler. WebCrawler has two methods to be overridden:
 *
 *  - boolean shouldVisit(page, url) which returns true if the page should be visited.
 *  - void visit(page) which determines what is done when visiting a page.
 *
 * Controller (this class) is a layer upon CrawlController. The primary purpose
 * is to set up the Part collection and retrieving it from the crawler.
 *
 * ExtendedCrawler is a layer upon WebCrawler. The primary purpose is to
 * include functionality for adding parts to a collection while crawling.
 */

public class Controller<T extends ExtendedCrawler> {

    private T crawler;
    private CrawlController controller;

    /* should probably not be configurable as we don't really want to run
     * the controllers multi-threaded; the crawler implementations are not
     * quite thread-safe.
     * (see the configure method, which sets a static field ...).
     */
    private static final int NUMBER_OF_CRAWLERS = 1;

    /**
     * Constructor for crawler of type T, sets config/crawler settings.
     * @param controller    The internal crawlcontroller to run the crawler.
     * @param crawler       Dummy instance of the crawler.
     * @param seeds         URL seeds with which to start the crawler.
     */
    public Controller(final CrawlController controller, final T crawler, final String[] seeds)
        throws IllegalArgumentException {

        if (seeds.length == 0) {
            throw new IllegalArgumentException(
                "Crawler must have at least one seed");
        }

        for (String seed : seeds) {
            controller.addSeed(seed);
        }

        this.crawler = crawler;
        this.controller = controller;

    }

    /**
     * Runs the crawler. Blocking operation.
     * @return  The collection of parts found by the crawler.
     */
    public List<Part> crawl() {

        List<Part> parts = new ArrayList<Part>();
        crawler.configure(parts);
        controller.start(crawler.getClass(), NUMBER_OF_CRAWLERS);
        return parts;

    }

    /**
     * Safely stops the crawl controller.
     */
    public void stop() {
        controller.shutdown();
        controller.waitUntilFinish();
    }
}
