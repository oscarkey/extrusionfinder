package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * An {@link IntentService} for submitting an image and loading results from the server
 */
public class CommsService extends IntentService {
    private static final String LOG_TAG = "CommsService";
    private static final String REST_ENDPOINT = "http://as2388.ddns.net:8080/extrusionFinder/rest/MatchServlet";

    public static final String ACTION_RESULTS_RECEIVED
            = "uk.ac.cam.cl.echo.extrusionfinder.client.action.RESULTS_RECEIVED";

    public static final String ACTION_REQUEST_ERROR
            = "uk.ac.cam.cl.echo.extrusionfinder.client.action.REQUEST_ERROR";
    public static final String EXTRA_REQUEST_ERROR_TYPE
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.REQUEST_ERROR_TYPE";
    public static final int ERROR_TYPE_NETWORK = 0;
    public static final int ERROR_TYPE_OTHER = 1;

    private static final String ACTION_REQUEST_RESULTS
            = "uk.ac.cam.cl.echo.extrusionfinder.client.action.REQUEST_RESULTS";
    public static final String EXTRA_REQUEST_UUID
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.REQUEST_UUID";


    private ResultsServiceAdapter resultsServiceAdapter;
    private ResultsCache resultsCache;


    /**
     * Starts this service to request results with the given image. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRequestResults(Context context, String uuid) {
        Intent intent = new Intent(context, CommsService.class);
        intent.setAction(ACTION_REQUEST_RESULTS);
        intent.putExtra(EXTRA_REQUEST_UUID, uuid);
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

        resultsCache = ResultsCache.getInstance(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        resultsCache.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REQUEST_RESULTS.equals(action)) {
                final String uuid = intent.getStringExtra(EXTRA_REQUEST_UUID);
                handleActionRequestResults(uuid);
            }
        }
    }

    /**
     * Request results for the image. This runs in a provided background thread.
     */
    private void handleActionRequestResults(String uuid) {
        // check if this request is the one we're currently look for results for
        if(!resultsCache.hasRequest(uuid)) {
            // give up, no longer interested in this request
            //TODO probably should handle this more gracefully
            return;
        }

        // get the image from the cache
        byte[] image = resultsCache.getImage(uuid);

        // encode the image as a base 64 string
        String base64Image = Base64.encodeToString(image, Base64.DEFAULT);

        // send a blocking request to the server to get results
        // blocking doesn't matter as we have our own thread
        List<Result> results;
        try {
            results = resultsServiceAdapter.getMatches(base64Image);
        }
        catch(RetrofitError e) {
            // print out the error
            Log.e(LOG_TAG, "Exception occurred when fetching results: " + e);

            // report it to the ui
            Intent intent = new Intent(ACTION_REQUEST_ERROR);
            intent.putExtra(EXTRA_REQUEST_UUID, uuid);
            if(e.getKind() == RetrofitError.Kind.NETWORK) {
                intent.putExtra(EXTRA_REQUEST_ERROR_TYPE, ERROR_TYPE_NETWORK);
            }
            else {
                intent.putExtra(EXTRA_REQUEST_ERROR_TYPE, ERROR_TYPE_OTHER);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            // give up loading
            return;
        }

        // store the results in the cache
        resultsCache.putResults(uuid, results);

        // send a local broadcast to notify the ui that we have received the results
        Intent intent = new Intent(ACTION_RESULTS_RECEIVED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
