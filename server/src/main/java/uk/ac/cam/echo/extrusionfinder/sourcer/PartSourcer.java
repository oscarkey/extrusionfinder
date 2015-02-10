package uk.ac.cam.echo.extrusionfinder.sourcer;

import uk.ac.cam.echo.extrusionfinder.parts.Part;
import uk.ac.cam.echo.extrusionfinder.sourcer.crawlers.*;
import uk.ac.cam.echo.extrusionfinder.database.IDBManager;
import uk.ac.cam.echo.extrusionfinder.database.MongoDBManager;

import java.util.Collection;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Program that crawls plastic extrusion vendors for information about the
 * extrusions they sell, in particular links and images.
 * The found extrusions + metadata is saved to a database.
 */
public class PartSourcer {

    /**
     * commandline invocation of the part sourcer.
     */
    public static void main(String[] args) throws Exception {

        String dbName = args.length > 0 ? args[0] : "extrusionDB";
        IDBManager db = new MongoDBManager(dbName);
        updateDatabase(db);
    }

    /**
     * Runs all the controllers (one per site)
     */
    public static void updateDatabase(IDBManager dbManager) throws Exception {

        Collection<Controller<? extends ExtendedCrawler>> crawlers =
            new ArrayList<Controller<? extends ExtendedCrawler>>();

        // This probably shouldn't be hardcoded in here, but configurable.
        // (mostly relevant if we add more websites to our crawler repertoire)
        crawlers.add(new Controller<SeagateCrawler>(SeagateCrawler.class,
            SeagateCrawler.SEEDS));

        for (Controller<? extends ExtendedCrawler> crawler : crawlers) {

            Stream<Part> stream = crawler.crawl();
            stream.forEach(p -> dbManager.savePart(p));

        }
    }
}
