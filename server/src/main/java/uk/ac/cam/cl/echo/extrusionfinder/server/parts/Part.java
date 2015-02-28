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
    protected String orderLink;

    /**
     * Constructor to be used by MongoJack only
     * @param _id            PartId used by database
     * @param manufacturerId Unique manufacturer identifier
     * @param partId         Part identifier used by manufacturer
     * @param link           Link to part on manufacturer's website
     * @param imageLink      Link to an image of the parts
     * @param orderLink      Link to order the part
     * @param size           Size of the part
     * @param description    Miscellaneous metadata information
     */
    @JsonCreator
    public Part(@JsonProperty("_id") String _id,
                @JsonProperty("manufacturerId") String manufacturerId,
                @JsonProperty("partId") String partId,
                @JsonProperty("link") String link,
                @JsonProperty("imageLink") String imageLink,
                @JsonProperty("size") Size size,
                @JsonProperty("description") String description,
                @JsonProperty("orderLink") String orderLink) {
        this._id = _id;
        this.manufacturerId = manufacturerId;
        this.partId = partId;
        this.link = link;
        this.imageLink = imageLink;
        this.orderLink = orderLink;
        this.size = size;
        this.description = description;
    }

    /**
     * Main constructor for creating parts.
     * @param manufacturerId Unique manufacturer identifier
     * @param partId         Part identifier used by manufacturer
     * @param link           Link to part on manufacturer's website
     * @param imageLink      Link to an image of the parts
     * @param orderLink      Link to order the part
     */
    public Part(String manufacturerId, String partId, String link, String imageLink, String orderLink) {
        this(manufacturerId + partId, manufacturerId, partId, link, imageLink,
            new Size(), "", orderLink);
    }

    /**
     * Constructor for creating parts with size and description.
     * @param manufacturerId Unique manufacturer identifier
     * @param partId         Part identifier used by manufacturer
     * @param link           Link to part on manufacturer's website
     * @param imageLink      Link to an image of the parts
     * @param orderLink      Link to order the part
     * @param size           Size of the part as listed by the manufacturer
     * @param description    Miscellaneous metadata
     */
    public Part(String manufacturerId, String partId, String link,
        String imageLink, String orderLink, Size size, String description) {

        this(manufacturerId + partId, manufacturerId, partId, link, imageLink,
            size, description, orderLink);
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

    public String getOrderLink() {
        return orderLink;
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
                    part.orderLink.equals(orderLink) &&
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
        return String.format("EXTRUSION-ID: %s; Description: %s; Size: %s",
            _id, description, size);
    }
}
