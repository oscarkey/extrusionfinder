// TEMPORARY!

package uk.ac.cam.echo.extrusionfinder.preprocessor;

public abstract class ImageData {
    public final byte[] data;
    public final int width;
    public final int height;

    public ImageData(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }
}
