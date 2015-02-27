package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.util.Base64;

import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;

/**
 * Created by oscar on 23/02/15.
 * Data structure to contain data for an image as it is transmitted to the server
 */
public class NetworkImage {
    private final String imageData;
    private final int width;
    private final int height;

    public NetworkImage(String imageData, int width, int height) {
        this.imageData = imageData;
        this.width = width;
        this.height = height;
    }

    /**
     * Convert RGBImageData into a NetworkImage
     * @param imageData RGBImage to convert
     * @return a new NetworkImage representing the image data
     */
    public static NetworkImage fromRGBImageData(RGBImageData imageData) {
        String encodedImage = Base64.encodeToString(imageData.data,
                Base64.NO_WRAP + Base64.NO_PADDING);

        return new NetworkImage(encodedImage, imageData.width, imageData.height);
    }
}
