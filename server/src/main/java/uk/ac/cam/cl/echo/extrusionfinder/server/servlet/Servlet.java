package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;

import java.io.IOException;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class Servlet implements IServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MatchedPart> findMatches(String encodedImage) throws IOException, ItemNotFoundException {
        // TODO: set image to decoded version of encodedImage
//        byte[] imageData = Base64.getDecoder().decode(encodedImage);
//        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
//
//        // TODO: Be prepared to call the image processor if filtering is to be done server side
//
//        IExtrusionFinder matcher = new ExtrusionFinder(); // TODO: set to a concrete implementer of IExtrusionMatcher
//        return matcher.findMatches(image, new MongoDBManager("extrusionDB"), 10,
//                new ZernikeManager(new MongoDBManager("extrusionDB")).getZernikeMoments());


        return null;
        //return null;
        // TODO: remove
//        List<MatchedPart> matches = new LinkedList<>();
//
//        matches.add(new MatchedPart(new Part("SG00", "SG", "00", "a_l", "http://"), 0.1));
//        matches.add(new MatchedPart(new Part("SG01", "SG", "01", "b_l", "http://"), 0.3));
//
//        return matches;
    }
}
