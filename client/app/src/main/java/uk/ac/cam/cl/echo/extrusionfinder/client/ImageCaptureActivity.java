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
import android.view.ViewGroup;

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
        public void onPreviewFrame(NV21ImageData image) {
            // check if the profile detection is ready
            // prevents attempting when busy or not loaded
            if(profileDetectionReady) {
                // process the frame
                profileDetectionReady = false;
                executePreprocessAsyncTask(image);
            }
        }

        @Override
        public void onPreviewAspectRatioCalculated(Dimension ratio) {
            setPreviewAspectRatio(ratio);
        }

        @Override
        public void onError(int errorType) {
            // show the error to the user
            showErrorDialog(errorType);
        }
    };

    /**
     * Set the size of the camera and processed preview surfaces to match what's produced by
     * the camera.
     * @param ratio Dimension representing the ratio
     */
    private void setPreviewAspectRatio(Dimension ratio) {
        // adjust the size of the surfaces to match the aspect ratio of the preview
        ViewGroup.LayoutParams previewLayout =
                findViewById(R.id.cameraPreviewSurface).getLayoutParams();
        ViewGroup.LayoutParams processedLayout =
                findViewById(R.id.processedImageSurface).getLayoutParams();

        // get the screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);

        // set the width to that of the screen
        previewLayout.width = screenSize.x;
        processedLayout.width = screenSize.x;

        // set the height to match the width
        double height = ((double)ratio.getWidth() / (double)ratio.getHeight()) * (double)screenSize.x;
        previewLayout.height = (int) height;
        processedLayout.height = (int) height;
    }

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
     * @param image NV21ImageData representing the image
     */
    private void executePreprocessAsyncTask(NV21ImageData image) {
        AsyncTask<Pair<NV21ImageData, SurfaceHolder>, Void, Void> preprocessAsyncTask
                = new AsyncTask<Pair<NV21ImageData, SurfaceHolder>, Void, Void>() {
            @Override
            protected Void doInBackground(Pair<NV21ImageData, SurfaceHolder>... params) {
                for(Pair<NV21ImageData, SurfaceHolder> param : params) {
                    // Unpack argument
                    byte[] encodedPreviewYUV = param.first.data;
                    SurfaceHolder surfaceHolder = param.second;

                    // We have three sizes:
                    //     - The preview image, previewWidth x previewHeight
                    //     - The image after preprocessing, processedWidth x processedHeight
                    //     - The buffer we are rendering to, surfaceWidth x surfaceHeight
                    int previewWidth  = param.first.width;
                    int previewHeight = param.first.height;
                    int minPreviewDim = Math.min(previewWidth, previewHeight);

                    int surfaceWidth  = surfaceHolder.getSurfaceFrame().width();
                    int surfaceHeight = surfaceHolder.getSurfaceFrame().height();
                    int minSurfaceDim = Math.min(surfaceWidth, surfaceHeight);

                    int processedWidth  = Configuration.PROFILE_DETECTION_STANDARD_IMAGE_SIZE;
                    int processedHeight = Configuration.PROFILE_DETECTION_STANDARD_IMAGE_SIZE;

                    // The image data received is rotated relative to the device orientation
                    // Unpack this from raw bytes to a JPEG stream to allow conversion to Bitmap.
                    YuvImage previewYUV = new YuvImage(
                            encodedPreviewYUV,
                            ImageFormat.NV21, // The passed-in format
                            previewWidth,     // YUV does not encode shape
                            previewHeight,    // YUV does not encode shape
                            null              // Strides deduced from data
                    );

                    ByteArrayOutputStream jpegStream = new ByteArrayOutputStream();
                    previewYUV.compressToJpeg(
                            new Rect(0, 0, previewWidth, previewHeight),
                            100,        // Quality
                            jpegStream  // Output stream
                    );

                    // Create a Bitmap and copy the bytes to it
                    Bitmap image = BitmapFactory.decodeByteArray(jpegStream.toByteArray(), 0, jpegStream.size());

                    ByteBuffer byteBuffer = ByteBuffer.allocate(image.getByteCount());
                    image.copyPixelsToBuffer(byteBuffer);

                    // Remove the alpha component from the decoded, rotated JPEG
                    // by ignoring every fourth byte
                    byte[] rgbData = byteBuffer.array();
                    for (int input = 0, output = 0; input < rgbData.length; /* alpha */ input++) {
                        rgbData[output++] = rgbData[input++];  // red
                        rgbData[output++] = rgbData[input++];  // green
                        rgbData[output++] = rgbData[input++];  // blue
                    }

                    // Do the preprocessing on the decoded, rotated RGB image
                    RGBImageData rgbImage = new RGBImageData(rgbData, previewWidth, previewHeight);
                    GrayscaleImageData processedImage = (new ProfileDetector()).process(rgbImage);

                    // Still rotated; convert grayscale to packed ARGB where the
                    // image is inverted and brightness becomes the alpha channel.
                    // The colour channels are completely black.
                    int[] rgbaData = new int[processedImage.data.length];
                    for (int i = 0; i < processedImage.data.length; i++) {
                        rgbaData[i] = (byte)(- processedImage.data[i] - 1) << 24;
                    }

                    // This allows us to create a bitmap which is then rotated
                    // to the correct orientation and scaled.
                    Matrix rotate = new Matrix();
                    rotate.setRotate(90);
                    rotate.postTranslate(minPreviewDim, 0);

                    // Generate, rotate, scale
                    Bitmap output;
                    output = Bitmap.createBitmap(rgbaData, processedWidth, processedWidth, Bitmap.Config.ARGB_8888);
                    output = Bitmap.createBitmap(output, 0, 0, processedWidth, processedWidth, rotate, false);
                    output = Bitmap.createScaledBitmap(output, minSurfaceDim, minSurfaceDim, false);

                    // Draw at 50% opacity, replacing the old image.
                    Paint translucent = new Paint();
                    translucent.setAlpha(128);
                    translucent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

                    // There need to be borders added to the larger axis.
                    int oblongness = surfaceWidth - surfaceHeight;
                    int xBorder = Math.max(0, +oblongness) / 2;
                    int yBorder = Math.max(0, -oblongness) / 2;

                    // Blit with borders onto the screen, display and unlock.
                    Canvas canvas = surfaceHolder.lockCanvas();
                    canvas.drawBitmap(output, xBorder, yBorder, translucent);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    profileDetectionReady = true;
                }

                return null;
            }
        };

        preprocessAsyncTask.execute(
                new Pair<>(image, processedImageSurface.getHolder()));
    }
}
