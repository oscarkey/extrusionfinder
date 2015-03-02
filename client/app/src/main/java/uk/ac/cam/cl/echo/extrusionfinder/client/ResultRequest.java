package uk.ac.cam.cl.echo.extrusionfinder.client;

import java.io.Serializable;
import java.util.List;

/**
 * Created by oscar on 08/02/15.
 * Basic data structure containing the request id, image and results when they arrive
 */
public class ResultRequest implements Serializable {
    private final String requestUuid;
    private final byte[] image;
    private List<Result> results;

    public ResultRequest(String requestId, byte[] image) {
        this.requestUuid = requestId;
        this.image = image;
    }

    public String getRequestUuid() {
        return requestUuid;
    }

    public byte[] getImage() {
        return image;
    }

    public List<Result> getResults() {
        return results;
    }

    public void putResults(List<Result> results) {
        this.results = results;
    }

    public boolean hasResults() {
        return (results != null);
    }
}
