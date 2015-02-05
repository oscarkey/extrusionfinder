package uk.ac.cam.cl.groupecho.extrusionfinder;

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
     */
    public void startCamera();

    /**
     * Calls the relevant methods to stop the camera.
     */
    public void stopCamera();

    /**
     * Must be called to set up the camera before starting it or taking a picture
     * @param previewSurface The surface where the preview from the camera should be displayed
     * @param capturedCallback Called when the image has been fully captured and provides byte data
     */
    public void setupCamera(SurfaceHolder previewSurface, ImageCapturedCallback capturedCallback);

    /**
     * Requests that the camera take a picture.
     * When this is complete the given callback will be called.
     * Camera must be setup and then started.
     */
    public void requestCapture();

    /**
     * Implemented to handle the completion of the image capture event.
     */
    public interface ImageCapturedCallback {
        /**
         * Called when the image has been completely captured
         * @param image The byte data of the image in a yet to be determined format
         */
        public void onImageCaptured(byte[] image);
    }
}
