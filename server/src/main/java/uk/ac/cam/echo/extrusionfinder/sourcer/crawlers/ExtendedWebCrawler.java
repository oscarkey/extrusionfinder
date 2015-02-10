package uk.ac.cam.echo.extrusionfinder.sourcer.crawlers;

import uk.ac.cam.echo.extrusionfinder.parts.Part;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import java.util.stream.Stream;

/**
 * Extends the crawler4j web crawler with, essentially, non-static information.
 */
public abstract class ExtendedWebCrawler extends WebCrawler {

    /**
     * Should update subclass with the parts stream such that the subclass puts
     * its results in the stream. Kind of hacky, but necessary due to the
     * staticness of the crawler4j implementation.
     */
    abstract void configure(Stream.Builder<Part> parts);

}
