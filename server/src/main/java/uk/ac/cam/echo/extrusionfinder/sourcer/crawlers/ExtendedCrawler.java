package uk.ac.cam.echo.extrusionfinder.sourcer.crawlers;

import uk.ac.cam.echo.extrusionfinder.parts.Part;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import java.util.stream.Stream;

/**
 * Extends the crawler4j web crawler with, essentially, dynamic information
 * injection for use in a static context ...
 */
public abstract class ExtendedCrawler extends WebCrawler {

    /**
     * @param parts The stream builder that extrusion parts are added to.
     * Should update subclass with the parts stream such that the subclass puts
     * its results in the stream. Kind of hacky, but necessary due to the
     * staticness of the crawler4j implementation.
     */
    abstract void configure(Stream.Builder<Part> parts);

    /**
     * @return  URL seeds for this crawler.
     */
    abstract String[] getSeeds();

}
