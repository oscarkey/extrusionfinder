package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import edu.uci.ics.crawler4j.crawler.CrawlController;

import java.lang.IllegalArgumentException;
import java.util.Collection;
import java.util.ArrayList;


/**
 * Controller for any web crawler. Manages configuration and parts stream.
 */

public class Controller<T extends ExtendedCrawler> {

    private T crawler;
    private CrawlController controller;

    private static final int NUMBER_OF_CRAWLERS = 1;

    /**
     * Constructor for crawler of type T, sets config/crawler settings.
     * @param controller    The internal crawlcontroller to run the crawler.
     * @param crawler       Dummy instance of the crawler.
     */
    public Controller(final CrawlController controller, final T crawler)
        throws IllegalArgumentException, CrawlerException {

        String[] seeds = crawler.getSeeds();
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
     * Starts the crawler. Blocking operation.
     * Not re-entrant as it is only possible to build the stream once!
     * @return  The stream of parts found by the crawler.
     */
    public Collection<Part> start() {

        Collection<Part> parts = new ArrayList<Part>();
        crawler.configure(parts);
        controller.start(crawler.getClass(), NUMBER_OF_CRAWLERS);
        return parts;

    }

    public void stop() {
        controller.shutdown();
        controller.waitUntilFinish();
    }
}
