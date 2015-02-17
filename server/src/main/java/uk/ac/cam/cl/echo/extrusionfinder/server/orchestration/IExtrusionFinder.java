package uk.ac.cam.cl.echo.extrusionfinder.server.orchestration;

import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author as2388
 */
public interface IExtrusionFinder {

    /**
     * Produces a list of best matches to the given extrusion image using the contents of the database.
     * @param inputImage Image to find matches for.
     * @param database   Database to load find matches in.
     * @param maxResults Maximum number of results to include in output list
     * @return           List of best matches found in the database
     */
    public List<MatchedPart> findMatches(BufferedImage inputImage, IDBManager database, int maxResults)
            throws ItemNotFoundException;
}
