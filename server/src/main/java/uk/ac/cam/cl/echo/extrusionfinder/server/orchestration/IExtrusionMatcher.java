package uk.ac.cam.cl.echo.extrusionfinder.server.orchestration;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author as2388
 */
public interface IExtrusionMatcher {

    /**
     * Produces a list of best matches to the given extrusion image using the contents of the database
     * @param inputImage Image to find matches for
     * @return           List of best matches
     */
    public List<MatchedPart> bestMatches(BufferedImage inputImage);
}
