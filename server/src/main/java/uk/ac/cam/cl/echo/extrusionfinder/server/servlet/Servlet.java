package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.orchestration.ExtrusionFinder;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
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
        // Decode image from base64 encoding to a BufferedImage via a byte array
        byte[] imageData = Base64.getDecoder().decode(encodedImage);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        // Find and return the best matches
        return ExtrusionFinder.findMatches(
                image, new MongoDBManager(Configuration.DEFAULT_DATABASE_NAME),
                Configuration.DEFAULT_NUMBER_OF_MATCHES
        );
    }
}
