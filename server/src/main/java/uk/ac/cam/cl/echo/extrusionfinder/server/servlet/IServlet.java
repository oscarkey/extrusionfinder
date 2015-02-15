package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import javax.ws.rs.*;
import java.util.List;

/**
 * Provides a RESTful API to clients for requesting potential matches to a given image
 * @author as2388
 */
@Path("/MatchServlet/")
@Produces("application/json")
public interface IServlet {

    @GET
    @Path("/matches/")
    public abstract List<MatchedPart> findMatches(@QueryParam("image") String image);
}
