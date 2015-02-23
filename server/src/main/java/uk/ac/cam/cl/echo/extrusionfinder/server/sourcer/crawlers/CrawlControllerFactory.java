package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/* it's a factory!*/
public class CrawlControllerFactory {

    /**
     * @return  Standard crawlcontroller
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
