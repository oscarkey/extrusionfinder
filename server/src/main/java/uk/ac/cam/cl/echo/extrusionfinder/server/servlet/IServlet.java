package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import javax.ws.rs.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Provides a RESTful API to clients for requesting potential matches to a given image
 * @author as2388
 */
@Path("/MatchServlet/")
@Produces("application/json")
public interface IServlet {

    @POST
    @Path("/matches/")
    public abstract List<MatchedPart> findMatches(String image) throws IOException, ItemNotFoundException;
}
