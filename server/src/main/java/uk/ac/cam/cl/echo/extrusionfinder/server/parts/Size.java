package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

public class Size {

    public enum Unit {
        IN, // inches
        MM, // millimeters
        UNKNOWN
    }

    /**
     * Dimension1 is either width/diameter (if one measurement), width (if two),
     * or outer diameter (if tube).
     */
    private float dimension1;

    /**
     * Dimension2 is either 0 (if one measurement), height (if two), or inner
     * diameter (if tube).
     */
    private float dimension2;

    /**
     * The unit of the measurements.
     */
    private Unit unit;

    /**
     * Default empty constructor. Measurements are set to 0, unit is unknown.
     */
    public Size() {
        dimension1 = 0;
        dimension2 = 0;
        unit = Unit.UNKNOWN;
    }

    /**
     * Constructor for two-dimensional size (e.g. width x height).
     */
    public Size(float dim1, float dim2, Unit unit) {
        this.dimension1 = Math.max(0, dim1);
        this.dimension2 = Math.max(0, dim2);
        this.unit = unit;
    }

    /**
     * Constructor for one-dimensional size (second dimension set to 0).
     */
    public Size(float dim1, Unit unit) {
        this(dim1, 0, unit);
    }

    /**
     * @return  Measurement of either width/diameter (if one measurement),
     *          width (if two), or outer diameter (if round part).
     */
    public float getDimension1() {
        return dimension1;
    }

    /**
     * @return  Measurement of either 0 (if one measurement), height (if two),
     *          or inner diameter (if round part).
     */
    public float getDimension2() {
        return dimension2;
    }

    /**
     * @ return Unit of the size measurements.
     */
    public Unit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Size)) {
            return false;
        }

        Size size = (Size)o;
        return dimension1 == size.getDimension1() &&
               dimension2 == size.getDimension2() &&
               unit.equals(size.getUnit());
    }

    @Override
    public String toString() {
        if (dimension1 <= 0) {
            return "Unknown size";
        } else if (dimension2 <= 0) {
            return String.format("%f %s", dimension1, unit.name());
        } else {
            return String.format("%f X %f %s", dimension1, dimension2, unit.name());
        }
    }

}
