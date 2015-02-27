package uk.ac.cam.cl.echo.extrusionfinder.client;

/**
 * Created by oscar on 21/02/15.
 * Immutable data structure to hold width and height.
 * Created as Size not available until API 21, and Camera.Size deprecated in 21
 */
public class Dimension {
    private final int width;
    private final int height;

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Dimension: width=" + getWidth() + " height=" + getHeight();
    }
}
