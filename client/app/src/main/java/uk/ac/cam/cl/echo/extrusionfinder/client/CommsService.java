package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} for submitting an image and loading results from the server
 */
public class CommsService extends IntentService {
    private static final String ACTION_REQUEST_RESULTS
            = "uk.ac.cam.cl.echo.extrusionfinder.client.action.REQUEST_RESULTS";

    private static final String EXTRA_IMAGE_BYTES
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.IMAGE_BYTES";
    private static final String EXTRA_REQUEST_UUID
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.REQUEST_UUID";

    /**
     * Starts this service to request results with the given image. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRequestResults(Context context, String uuid, byte[] image) {
        Intent intent = new Intent(context, CommsService.class);
        intent.setAction(ACTION_REQUEST_RESULTS);
        intent.putExtra(EXTRA_REQUEST_UUID, uuid);
        intent.putExtra(EXTRA_IMAGE_BYTES, image);
        context.startService(intent);
    }

    public CommsService() {
        super("CommsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REQUEST_RESULTS.equals(action)) {
                final String uuid = intent.getStringExtra(EXTRA_REQUEST_UUID);
                final byte[] image = intent.getByteArrayExtra(EXTRA_IMAGE_BYTES);
                handleActionRequestResults(uuid, image);
            }
        }
    }

    /**
     * Request results for the image. This runs in a provided background thread.
     */
    private void handleActionRequestResults(String uuid, byte[] image) {
        //TODO handle request results
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
