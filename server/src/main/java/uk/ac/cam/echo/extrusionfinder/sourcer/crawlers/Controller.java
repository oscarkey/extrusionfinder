package uk.ac.cam.echo.extrusionfinder.sourcer.crawlers;

import uk.ac.cam.echo.extrusionfinder.parts.Part;

import edu.uci.ics.crawler4j.crawler.CrawlController;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

/**
 * Controller for any web crawler. Manages configuration and parts stream.
 */

public class Controller<T extends ExtendedCrawler> {

    private T crawler;
    private Stream.Builder<Part> partsStream;
    private CrawlController controller;

    private static final int NUMBER_OF_CRAWLERS = 1;
    private static final String CRAWL_STORAGE_FOLDER = "~/data/crawl/root";

    /**
     * Constructor for crawler of type T, sets config/crawler settings.
     * @param crawlerClass  The class type of the site specific crawler.
     * @param seeds         urls to the pages that the crawler starts on.
     */
    public Controller(
        final CrawlController controller,
        final T crawler)
            throws Exception {

        for (String seed : crawler.getSeeds()) {
            controller.addSeed(seed);
        }

        Builder stream = Stream.builder();
        crawler.configure(stream);

        this.crawler = crawler;
        this.partsStream = stream;
        this.controller = controller;
    }

    /**
     * Starts the crawler. Blocking operation.
     * @return  The stream of parts found by the crawler.
     */
    public Stream<Part> start() {

        controller.start(crawler.getClass(), NUMBER_OF_CRAWLERS);
        return partsStream.build();

    }

    public void stop() {
        controller.shutdown();
        controller.waitUntilFinish();
    }
}
