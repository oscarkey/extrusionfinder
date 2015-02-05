package uk.ac.cam.echo.extrusionfinder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import uk.ac.cam.echo.extrusionfinder.parts.Part;

import javax.ws.rs.core.GenericType;
import java.util.List;

public class ClientDemo {
    /**
     * Makes a RESTful GET request to the server for a list of matches.
     * @param args
     */
    public static void main(String[] args) {
        // Create a new RESTEasy client and point it to the GET url.
        ResteasyClient c = new ResteasyClientBuilder().build();
        ResteasyWebTarget t = c.target("http://localhost:8080/extrusionFinder/rest/MatchServlet/matches");

        // Initiate the GET request. readEntity(new GenericType...) auto-deserializes the JSON into a list of Java part objects
        List<Part> parts = t.request().get().readEntity(new GenericType<List<Part>>(){});

        c.close();

        // Print some data from the returned list of parts to check that things worked
        System.out.println(parts.size());
        System.out.println(parts.get(0).get_id());
    }
}
