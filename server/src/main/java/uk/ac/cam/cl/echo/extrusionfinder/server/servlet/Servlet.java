package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.orchestration.ExtrusionFinder;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * {@inheritDoc}
 *
 * @author as2388
 */
public class Servlet implements IServlet {

    private static final Logger logger =
            LoggerFactory.getLogger(Servlet.class);


    /**
     * {@inheritDoc}
     */
    @Override
    public List<MatchedPart> findMatches(byte[] jpegData) throws IOException, ItemNotFoundException {
        logger.info("Endpoint hit");

        // Decode the JPEG
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(jpegData));

        // Remove the alpha component
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = image.getRGB(
            0, 0,           // Top left to read from
            width, height,  // How much to read 
            null,           // Preallocated array
            0,              // How much of preallocated array to skip
            width           // Stride of preallocated array
        );

        // Decompress pixels and remove alpha
        RGBImageData rgbImage = new RGBImageData(new byte[3 * width * height], width, height);

        int output = 0;
        for (int pixel : pixels) {
            rgbImage.data[output++] = (byte)(pixel >> 16);  // red
            rgbImage.data[output++] = (byte)(pixel >>  8);  // green
            rgbImage.data[output++] = (byte)(pixel >>  0);  // blue
        }

        logger.debug("Decoded");

        // Find and return the best matches
        List<MatchedPart> matchedParts = ExtrusionFinder.findMatches(
                rgbImage, new MongoDBManager(Configuration.DEFAULT_DATABASE_NAME),
                Configuration.DEFAULT_NUMBER_OF_MATCHES
        );

        logger.debug("Found matches");

        logger.info("Returning match data");

        return matchedParts;
    }
}
