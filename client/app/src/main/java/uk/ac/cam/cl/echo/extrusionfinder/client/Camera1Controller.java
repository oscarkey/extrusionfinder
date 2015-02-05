package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by oscar on 05/02/15.
 * The implementation of CameraController for the old, deprecated camera interface.
 * However, this is the only interface supported on phones running API <21, which is almost all of
 * them.
 */
@SuppressWarnings("deprecation") // good idea?
public class Camera1Controller implements CameraController {
    private static final String LOG_TAG = "Camera1Controller";

    private boolean isSetup;
    private boolean isPreviewing;
    private SurfaceHolder previewSurface;
    private ImageCapturedCallback capturedCallback;
    private Camera camera;

    @Override
    public void startCamera() {
        // check the camera has first been set up
        if(!isSetup) {
            throw new IllegalStateException("Camera must be set up before it can be started");
        }

        // open the camera, the rear camera is opened by default (we hope, should be)
        camera = Camera.open();

        // default orientation is landscape, so rotate this to portrait
        camera.setDisplayOrientation(90);

        // change settings to suit image recognition
        Camera.Parameters parameters = camera.getParameters();
        // TODO set parameters to suit the image recognition algorithm
        camera.setParameters(parameters);

        // connect the camera to the preview surface
        try {
            camera.setPreviewDisplay(previewSurface);
        }
        catch(IOException e) {
            //TODO display this error in a user friendly fashion
            Log.e(LOG_TAG, "Could not start the camera: " + e);
        }

        camera.startPreview();

        isPreviewing = true;
    }

    @Override
    public void stopCamera() {
        isPreviewing = false;

        camera.stopPreview();
        camera.release();
    }

    @Override
    public void setupCamera(SurfaceHolder previewSurface, ImageCapturedCallback capturedCallback) {
        // save references for later
        this.previewSurface = previewSurface;
        this.capturedCallback = capturedCallback;

        // register a callback on the surface holder to update the camera whenever it changes
        previewSurface.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // reattach the surface to the camera
                camera.stopPreview();
                try {
                    camera.setPreviewDisplay(holder);
                }
                catch(IOException e) {
                    //TODO display this error in a user friendly fashion
                    Log.e(LOG_TAG, "Could not reattach the preview to the camera: " + e);
                }
                camera.startPreview();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {}
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
        });

        isSetup = true;
    }

    @Override
    public void requestCapture() {
        // check the camera has been set up and is previewing
        if(!isPreviewing) {
            throw new IllegalStateException("Camera is not started thus we cannot capture.");
        }

        //TODO capture an image and return the bytes from the camera
    }
}
