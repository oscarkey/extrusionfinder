package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import java.util.Objects;

/**
 * TODO
 */
public class MatchedPart extends Part {
    protected final Double confidence;

    public MatchedPart(Part p, Double confidence) {
        super(
                p.get_id(),
                p.getManufacturerId(),
                p.getPartId(),
                p.getLink(),
                p.getImageLink(),
                p.getSize(),
                p.getDescription()
        );

        this.confidence = confidence;
    }

    public Double getConfidence() {
        return confidence;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatchedPart) {
            MatchedPart part = (MatchedPart) o;
            return  part._id.equals(_id) &&
                    part.manufacturerId.equals(manufacturerId) &&
                    part.partId.equals(partId) &&
                    part.link.equals(link) &&
                    part.imageLink.equals(imageLink) &&
                    Objects.equals(part.getConfidence(), confidence);
        } else {
            return false;
        }
    }
}
