package uk.ac.cam.cl.echo.extrusionfinder.client;

import java.io.Serializable;
import java.util.List;

/**
 * Created by oscar on 08/02/15.
 * Basic, immutable data structure containing the id of the request and also a list of the results.
 */
public class ResultSet implements Serializable {
    private final String requestUuid;
    private final List<Result> results;

    public ResultSet(String requestId, List<Result> results) {
        this.requestUuid = requestId;
        this.results = results;
    }

    public String getRequestUuid() {
        return requestUuid;
    }

    public List<Result> getResults() {
        return results;
    }
}
