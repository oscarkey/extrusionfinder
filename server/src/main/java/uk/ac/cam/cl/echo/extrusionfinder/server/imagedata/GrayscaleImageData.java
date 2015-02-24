package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

/**
 * Image storing grayscale pixels in bytes.
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
     * Load a grayscale image from a file.
     */
    public GrayscaleImageData(String filename) throws ImageLoadException {
        Mat input = Highgui.imread(filename, 1);
        width = input.cols();
        height = input.rows();
        if (width == 0 || height == 0) {
            throw new ImageLoadException();
        }
        data = new byte[width * height];
        input.get(0, 0, data);
    }
}
