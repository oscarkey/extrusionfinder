package uk.ac.cam.echo.extrusionfinder.parts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.cam.echo.extrusionfinder.database.DatabaseItem;

public class Part implements DatabaseItem {
    private String _id;
    private String link;
    private String imageLink;

    @JsonCreator
    public Part(@JsonProperty("_id") String _id,
                @JsonProperty("link") String link,
                @JsonProperty("imageLink") String imageLink) {
        this._id = _id;
        this.link = link;
        this.imageLink = imageLink;
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

    @Override
    public boolean equals(Object o) {
        Part part = (Part) o;
        return part._id.equals(_id) && part.link.equals(link) && part.imageLink.equals(imageLink);
    }
}
