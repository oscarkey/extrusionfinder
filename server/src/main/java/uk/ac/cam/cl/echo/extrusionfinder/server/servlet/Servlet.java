package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.util.Base64;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.orchestration.ExtrusionFinder;
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
 *
 * @author as2388
 */
public class Servlet implements IServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MatchedPart> findMatches(String jsonImage) throws IOException, ItemNotFoundException {
        System.out.println("Endpoint hit");

        // Deserialize the uploaded json to an UploadedImage
        ObjectMapper mapper = new ObjectMapper();
        UploadedImage uploadedImage = mapper.readValue(jsonImage, UploadedImage.class);

        RGBImageData rgbImage = new RGBImageData(
                uploadedImage.getData(), uploadedImage.getWidth(), uploadedImage.getHeight()
        );

        rgbImage.save("test.png");

        System.out.println("decoded");

        // Find and return the best matches
        return ExtrusionFinder.findMatches(
                rgbImage, new MongoDBManager(Configuration.DEFAULT_DATABASE_NAME),
                Configuration.DEFAULT_NUMBER_OF_MATCHES
        );

/*        System.out.println("returning");
        List<MatchedPart> results = new LinkedList<>();
        results.add(new MatchedPart(new Part("SG1971", "SG", "1971", "link",
                "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg1971.jpg"), 0.05));
        results.add(new MatchedPart(new Part("SG2022", "SG", "2022", "link",
                "http://seagateplastics.com/stock_plastics_catalog/images_catalog/sg2022.jpg"), 0.23));
        results.add(new MatchedPart(new Part("SG01", "SG", "01", "link", null), 0.09));
        return results;*/
    }
}
