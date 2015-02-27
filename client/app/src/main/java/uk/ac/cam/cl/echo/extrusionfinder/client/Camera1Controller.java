package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
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
    private static final int PICTURE_MIN_WIDTH = 600;
    private static final int PICTURE_MIN_HEIGHT = 600;

    private boolean isSetup;
    private boolean isPreviewing;
    private SurfaceHolder previewSurface;
    private CameraCallback callback;
    private Dimension desiredPreviewSize;
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
            // report the error and then give up starting
            Log.e(LOG_TAG, "Failed to open camera: " + e);
            callback.onError(CameraController.ERROR_TYPE_START);
            return;
        }

        camera.setPreviewCallback(previewCallback);
        // default orientation is landscape, so rotate this to portrait
        camera.setDisplayOrientation(90);


        // get the parameters, change them and then put them back again
        Camera.Parameters parameters = camera.getParameters();

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);


        Camera.Size bestPictureSize = getBestPictureSize(parameters.getSupportedPictureSizes());
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);

        // work out how big the preview should be and report this
        Dimension size = getBestPreviewSize(desiredPreviewSize, parameters.getSupportedPreviewSizes());
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
            Bitmap image = BitmapFactory.decodeByteArray(data, 0,data.length);
            image.copy(Bitmap.Config.ARGB_8888, false);

            ByteBuffer byteBuffer = ByteBuffer.allocate(image.getByteCount());
            image.copyPixelsToBuffer(byteBuffer);

            int width = image.getWidth();
            int height = image.getHeight();

            byte[] rgbData = byteBuffer.array();
            for (int input = 0, output = 0; output < 4 * width * height; output++) {
                rgbData[input++] = rgbData[output++];
                rgbData[input++] = rgbData[output++];
                rgbData[input++] = rgbData[output++];
            }

            RGBImageData rgb = new RGBImageData(rgbData, width, height);

            callback.onImageCaptured(rgb);
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
    private Dimension getBestPreviewSize(Dimension target, List<Camera.Size> options) {
        //TODO pick the best size
        // for now just pick the first size
        // switch width and height to convert between portrait and landscape
        int width = options.get(0).height;
        int height = options.get(0).width;
        return new Dimension(width, height);
    }

    /**
     * Choose the best picture size from a list of options
     * @param options List of Sizes to choose from
     * @return The best Size in the list
     */
    private Camera.Size getBestPictureSize(List<Camera.Size> options) {
        Collections.sort(options, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return (lhs.width * lhs.height) - (rhs.width * rhs.height);
            }
        });

        for(Camera.Size size : options) {
            if(size.width > PICTURE_MIN_WIDTH && size.height > PICTURE_MIN_HEIGHT) {
                return size;
            }
        }

        return options.get(options.size() - 1);
    }
}
