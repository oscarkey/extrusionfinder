package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import uk.ac.cam.cl.echo.extrusionfinder.server.orchestration.IExtrusionMatcher;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class Servlet implements IServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MatchedPart> findMatches(String encodedImage) {
        // TODO: set image to decoded version of encodedImage
        BufferedImage image = null;

        // TODO: Be prepared to call the image processor if filtering is to be done server side

        IExtrusionMatcher matcher = null; // TODO: set to a concrete implementer of IExtrusionMatcher
//        return matcher.bestMatches(image, null);
        return null;

        // TODO: remove
//        List<MatchedPart> matches = new LinkedList<>();
//
//        matches.add(new MatchedPart(new Part("SG00", "SG", "00", "a_l", "http://"), 0.1));
//        matches.add(new MatchedPart(new Part("SG01", "SG", "01", "b_l", "http://"), 0.3));
//
//        return matches;
    }
}
