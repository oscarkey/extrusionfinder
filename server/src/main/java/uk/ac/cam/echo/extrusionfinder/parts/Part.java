package uk.ac.cam.echo.extrusionfinder.parts;

public class Part {
    private String _id;
    private String link;
    private String image;

    public Part(String _id, String link, String image) {
        this._id = _id;
        this.link = link;
        this.image = image;
    }

    public Part(){}

    public String get_id() {
        return _id;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
