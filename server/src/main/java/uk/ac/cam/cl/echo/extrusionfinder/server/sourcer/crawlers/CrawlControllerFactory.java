package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * <p>Static provider of CrawlControllers with the only configuration options we
 * care about: storage folder, max crawl depth, max crawl pages.</p>
 * <p>Other things can be configured, but we don't need that range of flexibility.</p>
 * <p>Besides, setting up the CrawlController requires a lot of boilerplate code,
 * so we just stow it away in this nice, little factory.</p>
 */
public class CrawlControllerFactory {

    private CrawlControllerFactory(){}

    /**
     * @param storageFolder Path to the directory where the intermediate crawl data is stored.
     * @param maxCrawlDepth Maximum recursion level allowed. -1 for unlimited.
     * @param maxCrawlPages Maximum number of pages to be visited. -1 for unlimited.
     * @return  Standard crawlcontroller
     * @throws CrawlerException If the CrawlController constructor failed
     */
    public static CrawlController get(
        String storageFolder,
        int maxCrawlDepth,
        int maxCrawlPages) throws CrawlerException {

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
}
