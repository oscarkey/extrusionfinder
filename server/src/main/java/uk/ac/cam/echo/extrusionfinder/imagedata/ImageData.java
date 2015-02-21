package uk.ac.cam.echo.extrusionfinder.imagedata;

/**
 * Generic image storage class containing data and dimension information.
 */
public abstract class ImageData<T> {
    public final T data;
    public final int width;
    public final int height;

    public ImageData(T data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }
}
