package uk.ac.cam.echo.extrusionfinder.imagedata;

/**
 * Image storing Grayscale pixels in bytes.
 * <p>
 * Pixels are stored row by row in left-to-right, top-to-bottom order.
 */
public class RGBImageData extends ImageData<byte[]> {
    public RGBImageData(byte[] data, int width, int height) {
        super(data, width, height);
    }
}
