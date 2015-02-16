package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

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
    public List<Part> findMatches(String image) {
        // TODO: Make a call to the actual matcher for results
        List<Part> matches = new LinkedList<>();

        matches.add(new Part("SG", "00", "a_l", image));
        matches.add(new Part("SG", "01", "b_l", image));

        return matches;
    }
}
