package uk.ac.cam.echo.extrusionfinder.imagedata;

/**
 * Image storing RGB colour space in bytes.
 * <p>
 * Bytes store data for pixels as R, G, B, R, G, B, R, G, B, ...
 * <p>
 * Pixels are stored row by row in left-to-right, top-to-bottom order.
 */
public class GrayscaleImageData extends ImageData<byte[]> {
    /**
     * Construct an image with the given byte data, width and height.
     *
     * @param data (Reference) to image data. Other classes are responsible for initialising data.
     * @param width The width in pixels (not colour elements or subpixels).
     * @param height The height in pixels (not colour elements or subpixels).
     */
    public GrayscaleImageData(byte[] data, int width, int height) {
        super(data, width, height);
    }
}
