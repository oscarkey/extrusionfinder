package uk.ac.cam.cl.echo.extrusionfinder.server.imagedata;

import java.io.Serializable;

/**
 * Image storing Grayscale pixels in bytes.
 * <p>
 * Pixels are stored row by row in left-to-right, top-to-bottom order.
 */
public class RGBImageData extends ImageData<byte[]> implements Serializable {
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
