package uk.ac.cam.echo.extrusionfinder.parts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.cam.echo.extrusionfinder.database.DatabaseItem;

public class Part implements DatabaseItem {
    private String _id;
    private String manufacturerId;
    private String partId;
    private String link;
    private String imageLink;

    /**
     * Constructor to be used by MongoJack only
     * @param _id            PartId used by database
     * @param manufacturerId Unique manufacturer identifier
     * @param partId         Part identifier used by manufacturer
     * @param link           Link to part on manufacturer's website
     * @param imageLink      Link to an image of the parts
     */
    @JsonCreator
    public Part(@JsonProperty("_id") String _id,
                @JsonProperty("manufacturerId") String manufacturerId,
                @JsonProperty("partId") String partId,
                @JsonProperty("link") String link,
                @JsonProperty("imageLink") String imageLink) {
        this._id = _id;
        this.manufacturerId = manufacturerId;
        this.partId = partId;
        this.link = link;
        this.imageLink = imageLink;
    }

    /**
     * Main constructor for creating parts
     * @param manufacturerId Unique manufacturer identifier
     * @param partId         Part identifier used by manufacturer
     * @param link           Link to part on manufacturer's website
     * @param imageLink      Link to an image of the parts
     */
    public Part(String manufacturerId, String partId, String link, String imageLink) {
        this(manufacturerId + partId, manufacturerId, partId, link, imageLink);
    }

    @Override
    public String get_id() {
        return _id;
    }

    public String getLink() {
        return link;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public String getPartId() {
        return partId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Part) {
            Part part = (Part) o;
            return  part._id.equals(_id) &&
                    part.manufacturerId.equals(manufacturerId) &&
                    part.partId.equals(partId) &&
                    part.link.equals(link) &&
                    part.imageLink.equals(imageLink);
        } else {
            return false;
        }
    }
}
