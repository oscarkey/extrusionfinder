package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.*;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.Collection;
import java.util.ArrayList;
import java.util.stream.Stream;


/**
 * Program that crawls plastic extrusion vendors for information about the
 * extrusions they sell, in particular links and images.
 * The found extrusions + metadata is saved to a database.
 */
public class PartSourcer {

    /* crawl configuration */
    private static final String CRAWL_STORAGE_FOLDER = "crawlerdata/root";
    private static final int MAX_CRAWL_DEPTH = 5;
    private static final int MAX_CRAWL_PAGES = -1;


    /**
     * commandline invocation of the part sourcer.
     */
    public static void main(String[] args) throws Exception {

        String dbName = args.length > 0 ? args[0] : "extrusionDB";
        IDBManager db = new MongoDBManager(dbName);
        db.clearDatabase();
        updateDatabase(db);
    }

    /**
     * Runs all the controllers (one per site)
     */
    public static void updateDatabase(IDBManager dbManager) throws Exception {

        Collection<Controller<? extends ExtendedCrawler>> crawlers =
            new ArrayList<Controller<? extends ExtendedCrawler>>();

        crawlers.add(
            new Controller<SeagateCrawler>(
                getCrawlController(), // call this again per new crawler
                new SeagateCrawler()));

        for (Controller<? extends ExtendedCrawler> crawler : crawlers) {

            try {

                Stream<Part> stream = crawler.start();
                stream.forEach(p -> dbManager.savePart(p));

            } catch (Exception e) {

                throw e;

            } finally {

                crawler.stop();

            }
        }
    }

    /**
     * @return  Standard crawlcontroller to pass to the actual controller.
     * Note: do not reuse controllers for different crawlers; this is necessary
     * because once a seed is added, it can't be removed.
     */
    private static CrawlController getCrawlController() throws Exception {

        // set config options for controller (might need more!)
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);
        config.setMaxDepthOfCrawling(MAX_CRAWL_DEPTH);
        config.setMaxPagesToFetch(MAX_CRAWL_PAGES);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer =
            new RobotstxtServer(robotstxtConfig, pageFetcher);

        return new CrawlController(config, pageFetcher, robotstxtServer);
    }
}
