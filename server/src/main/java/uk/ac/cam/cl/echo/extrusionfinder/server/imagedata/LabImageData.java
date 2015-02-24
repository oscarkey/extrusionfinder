package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

/**
 * Image storing an L*a*b* colour space image in doubles.
 * <p>
 * Doubles store data for pixels as L*, a*, b*, L*, a*, b*, L*, a*, b*, ...
 * <p>
 * Pixels are stored row by row in left-to-right, top-to-bottom order.
 */
public class LabImageData extends ImageData<double[]> {
    /**
     * Construct an image with the given L*a*b* double data, width and height.
     *
     * @param data (Reference) to image data. Other classes are responsible for initialising data.
     * @param width The width in pixels (not colour elements or subpixels).
     * @param height The height in pixels (not colour elements or subpixels).
     */
    public LabImageData(double[] data, int width, int height) {
        super(data, width, height);
    }
}
