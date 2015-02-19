package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import java.util.Collection;

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
    public abstract void configure(Collection<Part> parts);

    /**
     * @return  URL seeds for this crawler.
     */
    public abstract String[] getSeeds();

}
