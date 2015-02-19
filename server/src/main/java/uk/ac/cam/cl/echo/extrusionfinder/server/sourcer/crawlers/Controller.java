package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

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
     * Constructor for crawler of type T, sets config/crawler settings.
     * @param storageFolder folder to store intermediate crawler data
     * @param maxCrawlDepth max recursion depth for the crawler
     * @param maxCrawlPages max number of pages crawled
     * @param crawler       Dummy instance of the crawler.
     */
    public Controller(
        final String storageFolder,
        final int maxCrawlDepth,
        final int maxCrawlPages,
        final T crawler)
        throws IllegalArgumentException, CrawlerException {

        this(getCrawlController(storageFolder, maxCrawlDepth, maxCrawlPages),
            crawler);
    }

    /**
     * @return  Standard crawlcontroller
     */
    private static CrawlController getCrawlController(String storageFolder,
        int maxCrawlDepth, int maxCrawlPages) throws CrawlerException {

        // set config options for controller (might need more!)
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(storageFolder);
        config.setMaxDepthOfCrawling(maxCrawlDepth);
        config.setMaxPagesToFetch(maxCrawlPages);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer =
            new RobotstxtServer(robotstxtConfig, pageFetcher);

        try {

            return new CrawlController(config, pageFetcher, robotstxtServer);

        } catch (Exception e) {

            throw new CrawlerException(e.getLocalizedMessage());

        }
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
