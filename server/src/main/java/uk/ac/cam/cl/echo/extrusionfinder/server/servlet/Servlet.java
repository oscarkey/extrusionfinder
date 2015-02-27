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

import java.io.IOException;
import java.util.List;

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
    public List<MatchedPart> findMatches(String jsonImage) throws IOException, ItemNotFoundException {
        logger.info("Endpoint hit");

        // Deserialize the uploaded json to an UploadedImage
        ObjectMapper mapper = new ObjectMapper();
        UploadedImage uploadedImage = mapper.readValue(jsonImage, UploadedImage.class);

        RGBImageData rgbImage = new RGBImageData(
                uploadedImage.getData(), uploadedImage.getWidth(), uploadedImage.getHeight()
        );

        logger.debug("Decoded");

        rgbImage.save("test.png");

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
