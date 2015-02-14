package uk.ac.cam.cl.echo.extrusionfinder.client;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by oscar on 10/02/15.
 * Interface used to communicate with the server via the restful api.
 * Implementation generated by Retrofit
 */
public interface ResultsServiceAdapter {
    /**
     * Get the results for a given image. Blocks until completion
     * @param image The image to find results for encoded as a base64 string
     * @return A list of the results matched
     */
    @POST("/matches")
    public List<Result> getMatches(@Body String image);
}
