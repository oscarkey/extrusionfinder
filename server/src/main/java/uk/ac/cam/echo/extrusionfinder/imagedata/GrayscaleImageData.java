package uk.ac.cam.echo.extrusionfinder.imagedata;

/**
 * Image storing RGB colour space in bytes.
 * <p>
 * Pixels go R, G, B, R, G, B, R, G, B, ...
 * <p>
 * Pixels are stored row by row in left-to-right, top-to-bottom order.
 */
public class GrayscaleImageData extends ImageData<byte[]> {
    public GrayscaleImageData(byte[] data, int width, int height) {
        super(data, width, height);
    }
}
