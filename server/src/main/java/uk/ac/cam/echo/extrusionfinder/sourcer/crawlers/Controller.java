package uk.ac.cam.echo.extrusionfinder.sourcer.crawlers;

import uk.ac.cam.echo.extrusionfinder.parts.Part;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * Controller for any web crawler. Manages configuration and parts stream.
 */

public class Controller<T extends ExtendedCrawler> {

    private Class<T> crawlerClass;
    private Stream.Builder<Part> partsStream;
    private CrawlController controller;

    private static final int NUMBER_OF_CRAWLERS = 1;
    private static final String CRAWL_STORAGE_FOLDER = "~/data/crawl/root";

    /**
     * Constructor for crawler of type T, sets config/crawler settings.
     * @param crawlerClass  The class type of the site specific crawler.
     * @param seeds         urls to the pages that the crawler starts on.
     */
    public Controller(final Class<T> crawlerClass, final String[] seeds)
        throws Exception {

        // set config options for controller (might need more!)
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);
        config.setMaxDepthOfCrawling(5);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer =
            new RobotstxtServer(robotstxtConfig, pageFetcher);

        // instantiate inner controller
        CrawlController controller =
            new CrawlController(config, pageFetcher, robotstxtServer);

        for (String seed : seeds) {
            controller.addSeed(seed);
        }

        this.crawlerClass = crawlerClass;
        this.partsStream = Stream.builder();
        this.controller = controller;

    }

    /**
     * Starts the crawler. Blocking operation.
     * @return  The stream of parts found by the crawler.
     */
    public Stream<Part> crawl() {

        try {

            // configure the crawler with the parts stream.
            // guaranteed this method exists due to T subclass constraint.
            // we need reflection because crawler4j uses class types not
            // instances.
            Method configure =
                crawlerClass.getMethod("configure", Stream.Builder.class);
            T dummy = (crawlerClass.getConstructor()).newInstance();
            configure.invoke(dummy, partsStream);

            controller.start(crawlerClass, NUMBER_OF_CRAWLERS);

        } catch (NoSuchMethodException e) { // TODO: all these!

            e.printStackTrace();

        } catch (IllegalAccessException e) {

            e.printStackTrace();

        } catch (InstantiationException e) {

            e.printStackTrace();

        } catch (InvocationTargetException e) {

            e.printStackTrace();
        }

        return partsStream.build();

    }
}
