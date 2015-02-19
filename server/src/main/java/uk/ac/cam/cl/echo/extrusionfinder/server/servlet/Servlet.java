package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import org.jboss.resteasy.util.Base64;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    public List<MatchedPart> findMatches(String encodedImage) throws IOException, ItemNotFoundException {
        encodedImage = encodedImage.replaceAll("\"", "");

        System.out.println("Endpoint hit");
        // Decode image from base64 encoding to a BufferedImage via a byte array
        byte[] imageData = Base64.decode(encodedImage);
        System.out.println("decoded");
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        // Find and return the best matches
//        return ExtrusionFinder.findMatches(
//                image, new MongoDBManager(Configuration.DEFAULT_DATABASE_NAME),
//                Configuration.DEFAULT_NUMBER_OF_MATCHES
//        );

        System.out.println("returning");
        List<MatchedPart> results = new LinkedList<>();
        results.add(new MatchedPart(new Part("SG00", "SG", "00", "link", "imagelink"), 0.05));
        results.add(new MatchedPart(new Part("SG01", "SG", "01", "link", "imagelink"), 0.09));
        return results;
    }
}
