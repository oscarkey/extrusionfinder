package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

/**
 * Image storing integers in unspecified format, one int per pixel.
 * <p>
 * Pixels are stored row by row in left-to-right, top-to-bottom order.
 */
public class IntGridImage extends ImageData<int[]> {
    /**
     * Construct an image with the given int data, width and height.
     *
     * @param data (Reference) to image data. Other classes are responsible for initialising data.
     * @param width The width in pixels.
     * @param height The height in pixels.
     */
    public IntGridImage(int[] data, int width, int height) {
        super(data, width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int index(int x, int y) {
        return y * width + x;
    }
}
