package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

/**
 * Image storing Grayscale pixels in bytes.
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

    /**
     * Get the position in data holding the value of pixel (x, y).
     */
    @Override
    public int index(int x, int y) {
        return y * width + x;
    }
}
