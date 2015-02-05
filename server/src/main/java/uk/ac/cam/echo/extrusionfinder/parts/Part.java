package uk.ac.cam.echo.extrusionfinder.parts;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Part {
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

    public String get_id() {
        return _id;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return imageLink;
    }

    @Override
    public boolean equals(Object o) {
        Part part = (Part) o;
        return part._id.equals(_id) && part.link.equals(link) && part.imageLink.equals(imageLink);
    }
}
