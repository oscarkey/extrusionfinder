package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;

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
    private CameraCallback callback;
    private Dimension desiredPreviewSize;
    private Dimension pictureSize;
    private Dimension previewSize;
    private Camera camera;

    @Override
    public void startCamera() {
        // check the camera has first been set up
        if(!isSetup) {
            throw new IllegalStateException("Camera must be set up before it can be started");
        }

        // open the camera, the rear camera is opened by default (we hope, should be)
        //TODO move this into async task to prevent blocking ui thread
        try {
            camera = Camera.open();
        }
        catch(RuntimeException e) {
            Log.e(LOG_TAG, "Failed to open camera: " + e);
            callback.onError(CameraController.ERROR_TYPE_START);
        }

        camera.setPreviewCallback(previewCallback);
        // default orientation is landscape, so rotate this to portrait
        camera.setDisplayOrientation(90);


        // get the parameters, change them and then put them back again
        Camera.Parameters parameters = camera.getParameters();

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.Size defaultPictureSize = parameters.getPictureSize();
        pictureSize = new Dimension(defaultPictureSize.width, defaultPictureSize.height);

        // work out how big the preview should be and report this
        Dimension size = getBestSize(desiredPreviewSize, parameters.getSupportedPreviewSizes());
        previewSize = size;
        callback.onSetPreviewSize(size);
        // have to swap the width and height because the camera assumes it is in landscape
        parameters.setPreviewSize(size.getHeight(), size.getWidth());

        camera.setParameters(parameters);


        // connect the camera to the preview surface
        try {
            camera.setPreviewDisplay(previewSurface);
        }
        catch(IOException e) {
            //TODO display this error in a user friendly fashion
            Log.e(LOG_TAG, "Could not start the camera: " + e);
            callback.onError(ERROR_TYPE_START);
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
    public void setupCamera(SurfaceHolder previewSurface, Dimension desiredPreviewSize,
                            CameraCallback callback) {
        // save references for later
        this.previewSurface = previewSurface;
        this.callback = callback;
        this.desiredPreviewSize = desiredPreviewSize;


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

        // request that the camera takes a picture
        // worth considering which callback we are using here
        //FIXME need to use raw and jpg in case one isn't available?
        camera.takePicture(null, null, pictureCallback);
    }

    private final Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            callback.onImageCaptured(new RGBImageData(data,
                    pictureSize.getWidth(), pictureSize.getHeight()));
        }
    };

    private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            callback.onPreviewFrame(new RGBImageData(data,
                    previewSize.getWidth(), previewSize.getHeight()));
        }
    };

    /**
     * Choose the best preview size from a list of options given a target
     * Note that target and return assume portrait display while options assumes landscape
     * @param target Dimension representing the target size for the preview (in portrait form)
     * @param options A List of Camera.Size options for the size of the preview (in landscape form)
     * @return The best size picked from the list (in portrait form)
     */
    private Dimension getBestSize(Dimension target, List<Camera.Size> options) {
        //TODO pick the best size
        // for now just pick the first size
        // switch width and height to convert between portrait and landscape
        int width = options.get(0).height;
        int height = options.get(0).width;
        return new Dimension(width, height);
    }
}
