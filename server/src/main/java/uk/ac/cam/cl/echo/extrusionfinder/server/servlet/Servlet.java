package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import org.jboss.resteasy.util.Base64;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

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
    public List<Part> findMatches(String image) throws IOException {
        image = image.replaceAll("\"", "").replaceAll("\\\\n", "");

        System.out.println(image);
        byte[] img = Base64.decode(image);

        // TODO: Make a call to the actual matcher for results
        List<Part> matches = new LinkedList<>();

        matches.add(new Part("SG00", "SG", "00", "a_l", image));
        matches.add(new Part("SG01", "SG", "01", "b_l", image));

        System.out.println("returning");

        return matches;
    }
}
