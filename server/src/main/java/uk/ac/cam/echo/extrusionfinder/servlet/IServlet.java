package uk.ac.cam.echo.extrusionfinder.servlet;

import uk.ac.cam.echo.extrusionfinder.parts.Part;

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
    public abstract List<Part> findMatches(@QueryParam("image") String image);
}
