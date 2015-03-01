package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.view.SurfaceHolder;

/**
 * Created by oscar on 05/02/15.
 * An interface to provide an abstraction over the two types of camera available depending on
 * Android version.
 */
public interface CameraController {
    public static final int ERROR_TYPE_START = 0;

    /**
     * Calls the relevant methods to start the camera.
     * The camera must first have been setup with a call to setup()
     * Camera must then be stopped when the activity is paused
     * @throws java.lang.IllegalStateException Thrown if called before setup.
     */
    public void startCamera();

    /**
     * Calls the relevant methods to stop the camera.
     */
    public void stopCamera();

    /**
     * Must be called to set up the camera before starting it or taking a picture
     * @param previewSurface The surface where the preview from the camera should be displayed
     * @param previewAspectRatio A Dimension representing the aspect ratio that we would like the
     *                           preview to be
     * @param cameraCallback Called when various camera events occur
     */
    public void setupCamera(SurfaceHolder previewSurface, Dimension previewAspectRatio,
                            CameraCallback cameraCallback);

    /**
     * Requests that the camera take a picture.
     * When this is complete the given callback will be called.
     * Camera must be setup and then started.
     * @throws java.lang.IllegalStateException Thrown if camera not started.
     */
    public void requestCapture();

    /**
     * Implemented to handle various camera events.
     */
    public interface CameraCallback {
        /**
         * Called when the image has been completely captured
         * @param image byte[] of the image as jpg
         */
        public void onImageCaptured(byte[] image);

        /**
         * Called every time a preview frame is delivered
         * @param image NV21ImageData representing the image
         */
        public void onPreviewFrame(NV21ImageData image);

        /**
         * Called when an error occurred with the camera
         * @param errorType A constant representing the error that occurred
         */
        public void onError(int errorType);
    }
}
