package uk.ac.cam.cl.echo.extrusionfinder.server.servlet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jboss.resteasy.util.Base64;

import java.io.IOException;

class UploadedImage {
    private final String base64ImageData;
    private final int width;
    private final int height;

    @JsonCreator
    public UploadedImage(
            @JsonProperty("imageData") String base64ImageData,
            @JsonProperty("width") int width,
            @JsonProperty("height") int height
    ) {
        this.base64ImageData = base64ImageData;
        this.width = width;
        this.height = height;
    }

    public byte[] getData() throws IOException {
        return Base64.decode(base64ImageData);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
