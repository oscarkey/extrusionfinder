package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.view.SurfaceHolder;

/**
 * Created by oscar on 05/02/15.
 * An interface to provide an abstraction over the two types of camera available depending on
 * Android version.
 */
public interface CameraController {

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
     * @param desiredPreviewSize A Dimension of the desired size of the preview
     * @param cameraCallback Called when various camera events occur
     */
    public void setupCamera(SurfaceHolder previewSurface, Dimension desiredPreviewSize,
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
         * @param image The byte data of the image in a yet to be determined format
         */
        public void onImageCaptured(byte[] image);

        /**
         * Called when the camera has been started and the preview size has been determined.
         * Should be used to set the surface holding the preview to the correct aspect ratio.
         * @param size The Dimension that has been chosen for the preview
         */
        public void onSetPreviewSize(Dimension size);
    }
}
