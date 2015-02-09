package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import uk.ac.cam.cl.groupecho.extrusionfinder.R;

public class ImageCaptureActivity extends ActionBarActivity {

    private static final String LOG_TAG = "ImageCaptureActivity";

    private CameraController cameraController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        addEventListeners();

        // test code to get the camera working
        // if the API level is >=21 (Lollipop), we can use the new camera api
        // otherwise use the old one
//        if(Build.VERSION.SDK_INT >= 21) {
        if(false) {

        }
        else {
            cameraController = new Camera1Controller();
            SurfaceView previewSurface = (SurfaceView) findViewById(R.id.cameraPreview);
            cameraController.setupCamera(previewSurface.getHolder(), imageCapturedCallback);
        }
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
                //TODO capture an image from the camera
                // launch the results activity
                ResultsActivity.startWithImage(button.getContext(), new byte[]{});
            }
        });
    }

    private final CameraController.ImageCapturedCallback imageCapturedCallback
            = new CameraController.ImageCapturedCallback() {
        @Override
        public void onImageCaptured(byte[] image) {
            Log.v(LOG_TAG, "Image was captured!");
        }
    };


}
