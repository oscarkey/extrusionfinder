package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import at.markushi.ui.CircleButton;
import uk.ac.cam.cl.echo.extrusionfinder.client.servercopies.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.client.servercopies.imagedata.GrayscaleImageData;
import uk.ac.cam.cl.echo.extrusionfinder.client.servercopies.imagedata.RGBImageData;
import uk.ac.cam.cl.echo.extrusionfinder.client.servercopies.preprocessor.ProfileDetector;
import uk.ac.cam.cl.groupecho.extrusionfinder.R;

public class ImageCaptureActivity extends Activity {

    private static final String LOG_TAG = "ImageCaptureActivity";

    private Context context;
    private CameraController cameraController;

    private boolean profileDetectionReady;
    private SurfaceView processedImageSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        context = this;

        addEventListeners();

        //TODO add camera2 support

        // get the size of the screen to use as the desired aspect ratio
        Display display = getWindowManager().getDefaultDisplay();
        Point pixelSize = new Point();
        display.getSize(pixelSize);
        Dimension screenSize = new Dimension(pixelSize.x, pixelSize.y);

        // create the camera and a surface to hold the preview
        cameraController = new Camera1Controller();
        SurfaceView previewSurface = (SurfaceView) findViewById(R.id.cameraPreviewSurface);
        previewSurface.setZOrderMediaOverlay(false);
        previewSurface.setZOrderOnTop(false);

        cameraController.setupCamera(previewSurface.getHolder(), screenSize, cameraCallback);

        // set the surface view which displays the
        processedImageSurface = (SurfaceView) findViewById(R.id.processedImageSurface);
        processedImageSurface.setZOrderMediaOverlay(true);
        processedImageSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);

        // load OpenCV
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, openCVLoaderCallback);
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
        CircleButton captureImageButton = (CircleButton) findViewById(R.id.captureImageButton);
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                //TODO indicate to the user that the picture has been taken
                // request that the camera takes an image
                cameraController.requestCapture();
            }
        });
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
        public void onPreviewFrame(byte[] image, Dimension size) {
            // check if the profile detection is ready
            // prevents attempting when busy or not loaded
            if(profileDetectionReady) {
                // process the frame
                profileDetectionReady = false;
                executePreprocessAsyncTask(image, size);
            }
        }

        @Override
        public void onError(int errorType) {
            // show the error to the user
            showErrorDialog(errorType);
        }
    };

    private void showErrorDialog(int errorType) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        // set the title and text of the dialog appropriately for the error
        switch(errorType) {
            case Camera1Controller.ERROR_TYPE_START:
                dialogBuilder.setTitle(R.string.title_dialog_error_camera_start);
                dialogBuilder.setMessage(R.string.message_dialog_error_camera_Start);
                break;
            default:
                throw new IllegalArgumentException("Unknown error type: " + errorType);
        }

        // add the ok button
        dialogBuilder.setNeutralButton(R.string.text_button_neutral, null);

        // detect the dialog being closed for some reason, probably by pressing ok
        // this has to be added to the dialog and not the builder to support older devices
        AlertDialog dialog = dialogBuilder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // exit the activity, and hopefully application
                finish();
            }
        });

        dialog.show();
    }

    /**
     * Callback for when OpenCV has been loaded
     */
    private final BaseLoaderCallback openCVLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if(status == BaseLoaderCallback.SUCCESS) {
                // on success flag that we are ready to process frames
                profileDetectionReady = true;
            }
            else {
                super.onManagerConnected(status);
            }
        }
    };

    /**
     * Creates and starts an async task to pre-process the image and draw the extrusion highlights
     * to a canvas.
     * @param image byte[] NV21 image to pre-process
     * @param size Dimension representing the size of the image
     */
    private void executePreprocessAsyncTask(byte[] image, Dimension size) {
        AsyncTask<Pair<Pair<byte[], Dimension>, SurfaceHolder>, Void, Void> preprocessAsyncTask
                = new AsyncTask<Pair<Pair<byte[], Dimension>, SurfaceHolder>, Void, Void>() {
            @Override
            protected Void doInBackground(Pair<Pair<byte[], Dimension>, SurfaceHolder>... params) {
                for(Pair<Pair<byte[], Dimension>, SurfaceHolder> param : params) {
                    byte[] encodedImage = param.first.first;
                    Dimension size = param.first.second;
                    SurfaceHolder surfaceHolder = param.second;
                    Canvas canvas = surfaceHolder.lockCanvas();

                    // get the dimensions of the raw image
                    int imWidth = size.getWidth();
                    int imHeight = size.getHeight();
                    int minDim = Math.min(imWidth, imHeight);

                    // get the dimensions of the surface to draw on
                    int surfaceWidth = surfaceHolder.getSurfaceFrame().width();
                    int surfaceHeight = surfaceHolder.getSurfaceFrame().height();
                    int minSurfaceDim = Math.min(surfaceWidth, surfaceHeight);

                    // perform conversion to jpeg, output will be rotated incorrectly
                    YuvImage yuv = new YuvImage(encodedImage, ImageFormat.NV21, imWidth, imHeight, null);

                    ByteArrayOutputStream jpegStream = new ByteArrayOutputStream();
                    yuv.compressToJpeg(new Rect(0, 0, imWidth, imHeight), 100, jpegStream);

                    Bitmap image = BitmapFactory.decodeByteArray(jpegStream.toByteArray(), 0, jpegStream.size());

                    // get the jpeg pixels as a byte array
                    ByteBuffer byteBuffer = ByteBuffer.allocate(image.getByteCount());
                    image.copyPixelsToBuffer(byteBuffer);

                    // remove the alpha component from the decoded jpeg
                    byte[] rgbData = byteBuffer.array();
                    for (int input = 0, output = 0; input < 4 * imWidth * imHeight; input++) {
                        rgbData[output++] = rgbData[input++];  // red
                        rgbData[output++] = rgbData[input++];  // green
                        rgbData[output++] = rgbData[input++];  // blue
                    }

                    // convert the byte array into an image and call the processor
                    RGBImageData rgbImage = new RGBImageData(rgbData, imWidth, imHeight);
                    GrayscaleImageData processedImage = (new ProfileDetector()).process(rgbImage);

                    // Scale it back up and output as int[], still rotated
                    int stride = Configuration.PROFILE_DETECTION_STANDARD_IMAGE_SIZE;

                    int xOffset = 0;
                    int yOffset = 0;
                    if (surfaceWidth > surfaceHeight) {
                        xOffset = (surfaceWidth - surfaceHeight) / 2;
                    } else {
                        yOffset = (surfaceHeight - surfaceWidth) / 2;
                    }

                    // Still rotated; convert grayscale to packed ARGB
                    int[] rgbaData = new int[processedImage.data.length];
                    for (int i = 0; i < processedImage.data.length; i++) {
                        rgbaData[i] = (byte)(- processedImage.data[i] - 1) << 24;
                    }

                    // Still rotated, scale bitmap up
                    Bitmap output = Bitmap.createBitmap(rgbaData, stride, stride, Bitmap.Config.ARGB_8888);

                    // Rotate the image to correct orientation
                    Matrix rotate = new Matrix();
                    rotate.setRotate(90);
                    rotate.postTranslate(minDim, 0);
                    output = Bitmap.createBitmap(output, 0, 0, stride, stride, rotate, false);

                    // Scale to screen size
                    output = Bitmap.createScaledBitmap(output, minSurfaceDim, minSurfaceDim, false);

                    // output onto screen
                    Paint translucent = new Paint();
                    translucent.setAlpha(128);
                    translucent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                    canvas.drawBitmap(output, xOffset, yOffset, translucent);

                    // draw the canvas
                    surfaceHolder.unlockCanvasAndPost(canvas);

                    // flag that we are ready to proces another frame
                    profileDetectionReady = true;
                }

                return null;
            }
        };

        preprocessAsyncTask.execute(
                new Pair<>(new Pair<>(image, size), processedImageSurface.getHolder()));
    }
}
