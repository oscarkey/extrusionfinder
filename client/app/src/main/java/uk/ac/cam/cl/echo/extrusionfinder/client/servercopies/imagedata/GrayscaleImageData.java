package uk.ac.cam.cl.echo.extrusionfinder.client.servercopies.imagedata;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

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
     *
     * @param filename Path to the image file.
     * @return A grayscaled version (if not already grayscale) image represented in the file.
     * @throws ImageLoadException The image could not be loaded, possibly due to incompatibility or
     *         a non-existant file.
     */
    public static GrayscaleImageData load(String filename) throws ImageLoadException {
        Mat input = Highgui.imread(filename, 0);
        int width = input.cols();
        int height = input.rows();
        if (width == 0 || height == 0) {
            throw new ImageLoadException();
        }
        byte[] data = new byte[width * height];
        input.get(0, 0, data);
        return new GrayscaleImageData(data, width, height);
    }

    /**
     * Saves a grayscale image to a file.
     *
     * @param filename Path to save the image to. Extension implies image format.
     */
    public void save(String filename) {
        // This might fail silently. I don't know yet.
        Mat output = new Mat(height, width, CvType.CV_8UC1);
        output.put(0, 0, data);
        Highgui.imwrite(filename, output);
    }
}
