package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.DatabaseItem;

public class Part implements DatabaseItem {

    protected String _id;
    protected String manufacturerId;
    protected String partId;
    protected String link;
    protected String imageLink;
    protected Size size;
    protected String description;

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
                @JsonProperty("imageLink") String imageLink,
                @JsonProperty("size") Size size,
                @JsonProperty("description") String description) {
        this._id = _id;
        this.manufacturerId = manufacturerId;
        this.partId = partId;
        this.link = link;
        this.imageLink = imageLink;
        this.size = size;
        this.description = description;
    }

    /**
     * Main constructor for creating parts.
     * @param manufacturerId Unique manufacturer identifier
     * @param partId         Part identifier used by manufacturer
     * @param link           Link to part on manufacturer's website
     * @param imageLink      Link to an image of the parts
     */
    public Part(String manufacturerId, String partId, String link, String imageLink) {
        this(manufacturerId + partId, manufacturerId, partId, link, imageLink, new Size(), "");
    }

    /**
     * Constructor for creating parts with size and description.
     * @param manufacturerId Unique manufacturer identifier
     * @param partId         Part identifier used by manufacturer
     * @param link           Link to part on manufacturer's website
     * @param imageLink      Link to an image of the parts
     * @param size           Size of the part as listed by the manufacturer
     * @param description    Miscellaneous metadata
     */
    public Part(String manufacturerId, String partId, String link,
        String imageLink, Size size, String description) {

        this(manufacturerId + partId, manufacturerId, partId, link, imageLink,
            size, description);
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

    public Size getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Part) {
            Part part = (Part) o;
            return  part._id.equals(_id) &&
                    part.manufacturerId.equals(manufacturerId) &&
                    part.partId.equals(partId) &&
                    part.link.equals(link) &&
                    part.imageLink.equals(imageLink) &&
                    part.size.equals(size) &&
                    part.description.equals(description);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("EXTRUSION-ID: %s; Description: %s, Size: %s",
            _id, description, size);
    }
}
