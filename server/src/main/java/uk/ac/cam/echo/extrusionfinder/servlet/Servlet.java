package uk.ac.cam.echo.extrusionfinder.servlet;

import uk.ac.cam.echo.extrusionfinder.parts.Part;

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

        matches.add(new Part("a", "a_l", image));
        matches.add(new Part("b", "b_l", image));

        return matches;
    }
}
