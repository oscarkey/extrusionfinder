package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.*;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Program that crawls plastic extrusion vendors for information about the
 * extrusions they sell, in particular links and images.
 * The found extrusions + metadata is saved to a database.
 */
public class PartSourcer {

    private static final Logger logger =
        LoggerFactory.getLogger(PartSourcer.class);

    /* crawl configuration TODO. move this to Configuration.java */
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

        System.out.println(db.loadPart("1SG1586"));
        System.out.println(db.loadPart("1SG1500"));
        System.out.println(db.loadPart("1SG1818"));
        System.out.println(db.loadPart("1SG2078"));
        System.out.println(db.loadPart("1TUBE 24"));
    }

    /**
     * Runs all the controllers (one per site)
     */
    public static void updateDatabase(IDBManager dbManager) throws CrawlerException {

        Collection<Controller<? extends ExtendedCrawler>> crawlers =
            new ArrayList<Controller<? extends ExtendedCrawler>>();

        try {

            crawlers.add(
                new Controller<SeagateCrawler>(
                    CRAWL_STORAGE_FOLDER, MAX_CRAWL_DEPTH, MAX_CRAWL_PAGES,
                    new SeagateCrawler()));

        } catch (CrawlerException | IllegalArgumentException e) {

            logger.error(e.getLocalizedMessage());
            System.exit(1);

        }

        updateDatabase(dbManager, crawlers);
    }

    /**
     * Crawls the passed crawlers for extrusions, saving the found parts in db.
     * @param dbManager Database manager for saving the found parts
     * @param crawlers  Collection of crawlers to be run
     */
    public static void updateDatabase(IDBManager dbManager,
        Collection<Controller<? extends ExtendedCrawler>> crawlers) {

        logger.info("Updating database...");

        for (Controller<? extends ExtendedCrawler> crawler : crawlers) {

            try {
                Collection<Part> parts = crawler.start();
                logger.info("There are " + parts.size() + " parts.");
                for (Part p : parts) {
                    dbManager.savePart(p);
                }

            } catch (Exception e) {

                logger.error(e.getLocalizedMessage());
                System.exit(1);

            } finally {

                crawler.stop();

            }
        }

    }
}
