package uk.ac.cam.cl.echo.extrusionfinder.client.servercopies.imagedata;

/**
 * Generic image storage class containing data and dimension information.
 *
 * The class only holds and transfers data, and does not allocate its own image data.
 */
public abstract class ImageData<T> {
    /** Image data. */
    public final T data;
    /** The width in pixels (not colour elements or subpixels). */
    public final int width;
    /** The height in pixels (not colour elements or subpixels). */
    public final int height;

    /**
     * Construct an image with the given data, width and height.
     *
     * @param data (Reference) to image data. Other classes are responsible for initialising data.
     * @param width The width in pixels (not colour elements or subpixels).
     * @param height The height in pixels (not colour elements or subpixels).
     */
    public ImageData(T data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }
}
