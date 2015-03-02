package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

/**
 * Image storing an XYZ colour space image in doubles.
 * <p>
 * Doubles store data for pixels as X, Y, Z, X, Y, Z, X, Y, Z, ...
 * <p>
 * Pixels are stored row by row in left-to-right, top-to-bottom order.
 */
public class XYZImageData extends ImageData<double[]> {
    /**
     * Construct an image with the given XYZ double data, width and height.
     *
     * @param data (Reference) to image data. Other classes are responsible for initialising data.
     * @param width The width in pixels (not colour elements or subpixels).
     * @param height The height in pixels (not colour elements or subpixels).
     */
    public XYZImageData(double[] data, int width, int height) {
        super(data, width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int index(int x, int y) {
        return 3 * (y * width + x);
    }
}
