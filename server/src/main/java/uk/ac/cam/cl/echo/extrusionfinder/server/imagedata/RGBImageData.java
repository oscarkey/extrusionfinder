package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

/**
 * Image storing an RGB colour space image in bytes.
 * <p>
 * Bytes store data for pixels as R, G, B, R, G, B, R, G, B, ...
 * <p>
 * Pixels are stored row by row in left-to-right, top-to-bottom order.
 */
public class RGBImageData extends ImageData<byte[]> {
    /**
     * Construct an image with the given RGB byte data, width and height.
     *
     * @param data (Reference) to image data. Other classes are responsible for initialising data.
     * @param width The width in pixels (not colour elements or subpixels).
     * @param height The height in pixels (not colour elements or subpixels).
     */
    public RGBImageData(byte[] data, int width, int height) {
        super(data, width, height);
    }
}
