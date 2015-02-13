package uk.ac.cam.cl.echo.extrusionfinder.client;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by oscar on 07/02/15.
 * Basic, immutable data structure to hold the part data of one result
 */
public class Result implements Serializable {
    @SerializedName("_id")
    private final String partId;
    private URI purchaseUri;

    public Result(String partId) {
        this.partId = partId;
    }

    public String getPartId() {
        return partId;
    }

    public URI getPurchaseUri() {
        return purchaseUri;
    }
}
