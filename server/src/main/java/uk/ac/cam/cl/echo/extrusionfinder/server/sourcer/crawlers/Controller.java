package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Controller for any web crawler. Manages configuration and parts stream.</p>
 *
 * <p>How it works using crawler4j:</p>
 * <p>crawler4j has the two classes CrawlController and WebCrawler.
 * CrawlController's start method is run with the class type of a class that
 * extends WebCrawler.</p>
 *
 * <p>Controller (this class) is a layer upon CrawlController. The primary purpose
 * is to set up the Part collection and retrieve it from the crawler.</p>
 *
 * <p>ExtendedCrawler is a layer upon WebCrawler. The primary purpose is to
 * include functionality for adding parts to a collection while crawling.</p>
 */

public class Controller<T extends ExtendedCrawler> {

    private T crawler;
    private CrawlController controller;

    /* This should probably not be configurable as we don't really want to run
     * the controllers multi-threaded; the crawler implementations are not
     * quite written for thread-safety anyway.
     * (see the configure method).
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
     * @return  The set of parts found by the crawler.
     */
    public Set<Part> crawl() {

        Set<Part> parts = new HashSet<Part>();
        crawler.configure(parts);
        controller.start(crawler.getClass(), NUMBER_OF_CRAWLERS);
        return parts;

    }

    /**
     * Safely stops the crawl controller. Cleans up resources, most useful when
     * the CrawlController is multi-threaded, so not too relevant for us.
     */
    public void stop() {
        controller.shutdown();
        controller.waitUntilFinish();
    }
}
