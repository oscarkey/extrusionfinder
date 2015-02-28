package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

/**
 * Provides a RESTful API to clients for requesting potential matches to a given image
 *
 * @author as2388
 */
@Path("/MatchServlet/")
@Produces("application/json")
public interface IServlet {

    @POST
    @Path("/matches/")
    @Consumes("image/jpeg")
    public abstract List<MatchedPart> findMatches(byte[] jpegData) throws IOException, ItemNotFoundException;
}
