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
    private Float dimension1;

    /**
     * Dimension2 is either 0 (if one measurement), height (if two), or inner
     * diameter (if tube).
     */
    private Float dimension2;

    /**
     * The unit of the measurements.
     */
    private Unit unit;

    /**
     * Constructor for two-dimensional size (e.g. width x height).
     * If the first argument is null, construct one-dimensional size.
     * If any of the arguments are less than 0, throw exception.
     * @param dim1  First dimension of the measurement. Non-negative.
     * @param dim2  Second dimension of the measurement. Non-negative.
     * @param unit  Unit of the measurement.
     */
    public Size(Float dim1, Float dim2, Unit unit) {

        if ((dim1 != null && dim1 < 0) || (dim2 != null && dim2 < 0)) {
            throw new IllegalArgumentException("negative size not allowed");
        }

        if (dim1 == null) {
            dimension1 = dim2;
        } else {
            dimension1 = dim1;
            dimension2 = dim2;
        }

        this.unit = unit;
    }

    /**
     * Constructor for one-dimensional size (second dimension set to null).
     * If first arg is less than 0, throw exception.
     * @param dim1  Measurement (length or diameter). Non-negative.
     * @param unit  Unit of the measurement.
     */
    public Size(Float dim1, Unit unit) {

        if (dim1 != null && dim1 < 0) {
            throw new IllegalArgumentException("negative size not allowed");
        }
        dimension1 = dim1;
        this.unit = unit;
    }

    /**
     * Default empty constructor. Measurements are null, unit is unknown.
     */
    public Size() {
        unit = Unit.UNKNOWN;
    }

    /**
     * @return  Measurement of either width/diameter (if one measurement),
     *          width (if two), or outer diameter (if round part).
     *          May return null.
     */
    public Float getDimension1() {
        return dimension1;
    }

    /**
     * @return  Measurement of either 0 (if one measurement), height (if two),
     *          or inner diameter (if round part).
     *          May return null.
     */
    public Float getDimension2() {
        return dimension2;
    }

    /**
     * @return Unit of the size measurements.
     */
    public Unit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || !(o instanceof Size)) {
            return false;
        }

        Size size = (Size) o;
        Float dim1 = size.getDimension1();
        Float dim2 = size.getDimension2();
        Unit unit1 = size.getUnit();

        boolean eqDim1 = (dimension1 == null && dim1 == null) ||
                         (dimension1 != null && dimension1.equals(dim1));
        boolean eqDim2 = (dimension2 == null && dim2 == null) ||
                         (dimension2 != null && dimension2.equals(dim2));
        boolean eqUnit = unit.equals(unit1);

        return eqDim1 && eqDim2 && eqUnit;
    }


    @Override
    public String toString() {
        if (dimension1 == null) {
            return "Unknown size";
        }

        if (dimension2 == null) {
            return String.format("%f %s", dimension1, unit.name());
        }

        return String.format("%f X %f %s", dimension1, dimension2, unit.name());
    }
}
