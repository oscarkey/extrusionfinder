package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Manufacturers;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Manufacturers.Name;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Manufacturer;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.Controller;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.CrawlControllerFactory;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.CrawlerException;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.ExtendedCrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * @param args  String array containing the arguments. First argument is the
     *              name of the database where the parts are saved. If no argument,
     *              use the default database name as specified in Configuration.
     */
    public static void main(String[] args) {

        String dbName = args.length > 0 ? args[0] : Configuration.DEFAULT_DATABASE_NAME;

        try {

            IDBManager db = new MongoDBManager(dbName);
            db.clearDatabase();

            // run the crawlers and put the found extrusions into the database
            Collection<Controller<? extends ExtendedCrawler>> crs = getControllers();
            updateDatabase(db, crs);

        } catch (UnknownHostException
                |CrawlerException
                |IllegalArgumentException e) {

            logger.error(e.getLocalizedMessage());

        }

    }

    /**
     * @return A collection of all crawlers as found in the static Manufacturers class.
     * @throws CrawlerException         if the controller constructor failed.
     * @throws IllegalArgumentException if the manufacturer seed array was empty.
     */
    public static Collection<Controller<? extends ExtendedCrawler>> getControllers()
        throws CrawlerException, IllegalArgumentException {

        // initialise collection of website crawlers

        Collection<Controller<? extends ExtendedCrawler>> controllers =
            new ArrayList<Controller<? extends ExtendedCrawler>>();

        // get config options from Configuration file
        String dir = Configuration.getCrawlStorageFolder();
        int maxDepth = Configuration.getMaxCrawlDepth();
        int maxPages = Configuration.getMaxCrawlPages();

        // get manufacturers
        Map<Name, Manufacturer> mans = Manufacturers.getAll();

        for (Manufacturer manufacturer : mans.values()) {

            ExtendedCrawler crawler = manufacturer.getCrawler();
            String[] seeds = manufacturer.getSeeds();

            // NOTE: must call CrawlControllerFactory again for each
            // crawler! Each crawler must have their own CrawlController
            // instance. (crawlcontroller is the crawler4j controller)

            Controller<? extends ExtendedCrawler> c =
                new Controller<ExtendedCrawler>(
                    CrawlControllerFactory.get(dir, maxDepth, maxPages),
                    crawler,
                    seeds
                );

            controllers.add(c);
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
                List<Part> parts = c.crawl();
                String msg = String.format("Found %d parts.", parts.size());
                logger.info(msg);

                for (Part p : parts) {
                    dbManager.savePart(p);
                }

            } finally {

                // cleanup; it's nice and safe and all, but not strictly
                // necessary, since if run through the commandline, this
                // program will be closing immediately afterwards anyway.
                c.stop();

            }
        }
    }
}
