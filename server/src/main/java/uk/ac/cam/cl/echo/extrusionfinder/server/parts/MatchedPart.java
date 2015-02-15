package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

/**
 * TODO
 */
public class MatchedPart extends Part {
    private final Double confidence;

    public MatchedPart(Part p, Double confidence) {
        super(
                p.get_id(),
                p.getManufacturerId(),
                p.getPartId(),
                p.getLink(),
                p.getImageLink()
        );

        this.confidence = confidence;
    }

    public Double getConfidence() {
        return confidence;
    }
}
