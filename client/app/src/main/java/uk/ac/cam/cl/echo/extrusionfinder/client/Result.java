package uk.ac.cam.cl.echo.extrusionfinder.client;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscar on 07/02/15.
 * Basic, immutable data structure to hold the part data of one result
 */
public class Result implements Serializable {
    // properties given by server
    @SerializedName("_id")
    private final String id;
    private String manufacturerId;
    private String partId;
    private String link;
    private String imageLink;
    private String description;

    public Result(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public String getPartId() {
        return partId;
    }

    public String getLink() {
        return link;
    }

    public String getImageLink() {
        return imageLink;
    }

    public boolean hasImageLink() {
        return (imageLink != null);
    }

    public String getDescription() {
        return description;
    }
}
