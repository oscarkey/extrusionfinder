package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uk.ac.cam.cl.groupecho.extrusionfinder.R;

public class ImageCaptureActivity extends ActionBarActivity {

    private static final String LOG_TAG = "ImageCaptureActivity";

    private Context context;
    private CameraController cameraController;
    private SurfaceView previewSurface;
    private Dimension screenSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        context = this;

        addEventListeners();

        //TODO add camera2 support

        // get the size of the screen
        //FIXME should probably use window not screen size?
        Display display = getWindowManager().getDefaultDisplay();
        Point pixelSize = new Point();
        display.getSize(pixelSize);
        screenSize = new Dimension(pixelSize.x, pixelSize.y);

        // create the camera and a surface to hold the preview
        cameraController = new Camera1Controller();
        previewSurface = (SurfaceView) findViewById(R.id.cameraPreviewSurface);

        cameraController.setupCamera(previewSurface.getHolder(), screenSize, cameraCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraController.startCamera();
    }

    @Override
    protected void onPause() {
        cameraController.stopCamera();

        super.onPause();
    }

    private void addEventListeners() {
        // capture image button: take a picture
        Button captureImageButton = (Button) findViewById(R.id.captureImageButton);
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                //TODO indicate to the user that the picture has been taken
                // request that the camera takes an image
                cameraController.requestCapture();
            }
        });
    }

    /**
     * Set the size of the preview surface to fit the given aspect ratio.
     * Should be linked to CameraController's onSetPreviewSize
     * @param aspectRatio A Dimension which is of the desired aspect ratio
     */
    private void setPreviewSize(Dimension aspectRatio) {
        // adjust the size of the surface to match the aspect ratio of the preview
        ViewGroup.LayoutParams layoutParams = previewSurface.getLayoutParams();

        // set the width to that of the screen
        layoutParams.width = screenSize.getWidth();

        // set the height to match the width
        double height = ((double)aspectRatio.getHeight() / (double)aspectRatio.getWidth()) * (double)screenSize.getWidth();
        layoutParams.height = (int) height;
    }

    private final CameraController.CameraCallback cameraCallback
            = new CameraController.CameraCallback() {

        @Override
        public void onImageCaptured(byte[] image) {
            // save the request to the cache
            ResultsCache cache = ResultsCache.getInstance(context);
            String uuid = cache.putRequest(image);
            cache.close();

            // launch the results activity
            ResultsActivity.startWithUuid(context, uuid);
        }

        @Override
        public void onSetPreviewSize(Dimension size) {
            setPreviewSize(size);
        }
    };


}
