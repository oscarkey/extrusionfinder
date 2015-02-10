package uk.ac.cam.echo.extrusionfinder.sourcer;

import uk.ac.cam.echo.extrusionfinder.parts.Part;
import uk.ac.cam.echo.extrusionfinder.sourcer.crawlers.*;

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

        updateDatabase();

    }

    /**
     * Runs all the controllers (one per site)
     * TODO: pass IDBManager as argument, instead of printing links, save parts
     */
    public static void updateDatabase() throws Exception {

        Collection<Controller<? extends ExtendedWebCrawler>> crawlers =
            new ArrayList<Controller<? extends ExtendedWebCrawler>>();

        // This probably shouldn't be hardcoded in here, but configurable.
        // (mostly relevant if we add more websites to our crawler repertoire)
        crawlers.add(new Controller<SeagateCrawler>(SeagateCrawler.class,
            SeagateCrawler.SEEDS));

        for (Controller<? extends ExtendedWebCrawler> crawler : crawlers) {

            Stream<Part> stream = crawler.crawl();
            //stream.forEach(p -> dbManager.savePart(p));
            stream.forEach(p -> System.out.println(p.getLink()));

        }
    }
}
