package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

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

    /**
     * Load an RGB image from a file.
     *
     * @param filename Path to the image file.
     * @return The rgb image represented in the file.
     * @throws ImageLoadException The image could not be loaded, possibly due to incompatibility or
     *         a non-existant file.
     */
    public static RGBImageData load(String filename) throws ImageLoadException {
        Mat input = Highgui.imread(filename, 1);
        int width = input.cols();
        int height = input.rows();
        if (width == 0 || height == 0) {
            throw new ImageLoadException();
        }
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);
        byte[] data = new byte[width * height * 3];
        input.get(0, 0, data);
        return new RGBImageData(data, width, height);
    }

    /**
     * Saves an RGB image to a file.
     *
     * @param filename Path to save the image to. Extension implies image format.
     */
    public void save(String filename) {
        try {
            nu.pattern.OpenCV.loadShared();
        } catch (Throwable e) {}

        // This might fail silently. I don't know yet.
        Mat output = new Mat(height, width, CvType.CV_8UC3);
        output.put(0, 0, data);
        Imgproc.cvtColor(output, output, Imgproc.COLOR_RGB2BGR);
        Highgui.imwrite(filename, output);
    }
}
