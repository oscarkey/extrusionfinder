package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import edu.uci.ics.crawler4j.crawler.CrawlController;

import java.lang.IllegalArgumentException;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;


/**
 * Controller for any web crawler. Manages configuration and parts stream.
 */

public class Controller<T extends ExtendedCrawler> {

    private T crawler;
    private CrawlController controller;

    private static final int NUMBER_OF_CRAWLERS = 1;
    private static final String CRAWL_STORAGE_FOLDER = "~/crawlerdata/root";

    /**
     * Constructor for crawler of type T, sets config/crawler settings.
     * @param controller    A crawler4j CrawlController to run the whole thing.
     * @param crawler       Dummy instance of the crawler.
     */
    public Controller(final CrawlController controller, final T crawler)
        throws IllegalArgumentException {

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
    public Stream<Part> start() {

        Builder<Part> stream = Stream.builder();
        crawler.configure(stream);
        controller.start(crawler.getClass(), NUMBER_OF_CRAWLERS);
        return stream.build();

    }

    public void stop() {
        controller.shutdown();
        controller.waitUntilFinish();
    }
}