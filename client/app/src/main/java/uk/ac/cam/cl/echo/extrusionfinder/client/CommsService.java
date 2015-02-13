package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;

import java.util.List;

import retrofit.RestAdapter;

/**
 * An {@link IntentService} for submitting an image and loading results from the server
 */
public class CommsService extends IntentService {
    private static final String LOG_TAG = "CommsService";
    private static final String REST_ENDPOINT = "http://as2388.ddns.net:8080/extrusionFinder/rest/MatchServlet";

    public static final String ACTION_RESULTS_RECEIVED
            = "uk.ac.cam.cl.echo.extrusionfinder.client.action.RESULTS_RECEIVED";

    private static final String ACTION_REQUEST_RESULTS
            = "uk.ac.cam.cl.echo.extrusionfinder.client.action.REQUEST_RESULTS";

    private static final String EXTRA_IMAGE_BYTES
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.IMAGE_BYTES";
    private static final String EXTRA_REQUEST_UUID
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.REQUEST_UUID";


    private ResultsServiceAdapter resultsServiceAdapter;


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
    public void onCreate() {
        super.onCreate();

        // generate the implementation of the ResultService interface using retrofit
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(REST_ENDPOINT)
                .build();
        resultsServiceAdapter = restAdapter.create(ResultsServiceAdapter.class);
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
        // encode the image as a base 64 string
        String base64Image = Base64.encodeToString(image, Base64.DEFAULT);

        // send a blocking request to the server to get results
        // blocking doesn't matter as we have our own thread
        List<Result> results = resultsServiceAdapter.getMatches(base64Image);

        // store the results in the cache
        ResultsCache resultsCache = ResultsCache.getInstance(this);
        resultsCache.putResults(uuid, results);

        // send a local broadcast to notify the ui that we have received the results
        Intent intent = new Intent(ACTION_RESULTS_RECEIVED);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
    }
}
