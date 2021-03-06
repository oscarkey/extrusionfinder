package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import edu.uci.ics.crawler4j.crawler.WebCrawler;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import java.util.Set;

/**
 * Extends the crawler4j web crawler with, essentially, dynamic information
 * injection for use in a static context ...
 */
public abstract class ExtendedCrawler extends WebCrawler {

    /**
     * @param parts         The set that extrusion parts are added to.
     * Should update subclass with the parts set such that the subclass puts
     * its results in the set. Kind of hacky, but necessary due to the
     * staticness of the crawler4j implementation.
     */
    public abstract void configure(Set<Part> parts);
}
