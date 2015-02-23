package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.Controller;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.CrawlControllerFactory;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.CrawlerException;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.ExtendedCrawler;


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

    /**
     * commandline invocation of the part sourcer.
     */
    public static void main(String[] args) throws Exception {

        String dbName = args.length > 0 ? args[0] : "extrusionDB";
        IDBManager db = new MongoDBManager(dbName);
        db.clearDatabase();

        // run the crawlers and put the found extrusions into the database
        Collection<Controller<? extends ExtendedCrawler>> crs = getControllers();
        updateDatabase(db, crs);

        System.out.println(db.loadPart("1SG1586"));
        System.out.println(db.loadPart("1SG1500"));
        System.out.println(db.loadPart("1SG1818"));
        System.out.println(db.loadPart("1SG2078"));
        System.out.println(db.loadPart("1TUBE 24"));
    }

    /**
     * @return  A collection of all currently written crawlers (ie. hardcoded)
     */
    public static Collection<Controller<? extends ExtendedCrawler>> getControllers() {

        // initialise collection of website crawlers

        Collection<Controller<? extends ExtendedCrawler>> controllers =
            new ArrayList<Controller<? extends ExtendedCrawler>>();

        // get config options from Configuration file

        String dir = Configuration.getCrawlStorageFolder();
        int dp = Configuration.getMaxCrawlDepth();
        int pg = Configuration.getMaxCrawlPages();
        Collection<? extends ExtendedCrawler> crawlers = Configuration.getCrawlers();

        try {

            for (ExtendedCrawler crawler : crawlers) {

                // NOTE: must call CrawlControllerFactory again for each
                // crawler! Each crawler must have their own CrawlController
                // instance. (crawlcontroller is the crawler4j controller)

                Controller<? extends ExtendedCrawler> c =
                    new Controller<ExtendedCrawler>(
                        CrawlControllerFactory.get(dir, dp, pg),
                        crawler
                    );

                controllers.add(c);
            }


        } catch (CrawlerException | IllegalArgumentException e) {

            logger.error(e.getLocalizedMessage());
            System.exit(1);

        }

        return controllers;
    }

    /**
     * Crawls the passed crawlers for extrusions, saving the found parts in db.
     * @param dbManager Database manager for saving the found parts
     * @param crawlers  Collection of crawlers to be run
     */
    public static void updateDatabase(IDBManager dbManager,
        Collection<Controller<? extends ExtendedCrawler>> controllers) {

        logger.info("Updating database...");

        for (Controller<? extends ExtendedCrawler> c : controllers) {

            try {
                Collection<Part> parts = c.start();
                logger.info("There are " + parts.size() + " parts.");

                for (Part p : parts) {
                    dbManager.savePart(p);
                }

            } catch (Exception e) {

                logger.error(e.getLocalizedMessage());
                System.exit(1);

            } finally {

                c.stop();

            }
        }
    }
}
