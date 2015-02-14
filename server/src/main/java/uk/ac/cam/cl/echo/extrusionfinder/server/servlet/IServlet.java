package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import org.jboss.resteasy.annotations.Body;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import javax.ws.rs.*;
import java.io.IOException;
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
    @Consumes("*/*")
    public abstract List<Part> findMatches(String image) throws IOException;
}
