package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;
import java.util.UUID;

import uk.ac.cam.cl.groupecho.extrusionfinder.R;

public class ResultsActivity extends ActionBarActivity {

    private static final String LOG_TAG = "ResultsActivity";

    private static final String EXTRA_IMAGE_BYTES
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.IMAGE_BYTES";
    private static final String EXTRA_REQUEST_UUID
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.REQUEST_UUID";

    private String requestUuid;
    private ResultsCache resultsCache;
    private BroadcastReceiver resultsReceivedBroadcastReceiver;
    private ListView listView;

    /**
     * Send an intent to start the ResultsActivity
     * @param context a context
     * @param image the image byte data array to get results for
     */
    public static void startWithImage(Context context, byte[] image) {
        Intent intent = new Intent(context, ResultsActivity.class);

        // pass the image bytes
        intent.putExtra(EXTRA_IMAGE_BYTES, image);

        // give the request an id to allow us to cache it
        String uuid = UUID.randomUUID().toString();
        intent.putExtra(EXTRA_REQUEST_UUID, uuid);

        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // get a reference to the list view
        listView = (ListView) findViewById(R.id.resultsListView);

        // initially show a progress bar/spinner
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.resultsProgressBar);
        listView.setEmptyView(progressBar);

        // set up a broadcast receiver so we are notified when new results arrive
        setupBroadcastReceiver();

        // get the request id from the intent
        requestUuid = getIntent().getStringExtra(EXTRA_REQUEST_UUID);

        // check the cache to see if we have already have data for this request
        resultsCache = ResultsCache.getInstance(this);
        if(resultsCache.hasResults(requestUuid)) {
            displayResults(resultsCache.getResults(requestUuid));
        }
        else {
            // otherwise load from the service
            // get the image from the intent
            byte[] image = getIntent().getByteArrayExtra(EXTRA_IMAGE_BYTES);

            // launch the service to request the results
            // these are stored in the cache and then we get a broadcast
            CommsService.startActionRequestResults(this, requestUuid, image);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        resultsCache.close();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(resultsReceivedBroadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                // when the home button is pressed go back to the image capture activity
                Intent homeIntent = new Intent(this, ImageCaptureActivity.class);
                // make such we remove the results activity from the stack
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Display the given results in the list view. Any existing results will be removed.
     * @param results List of results to display
     */
    private void displayResults(List<Result> results) {
        // create a list adapter with the results and attach it to the list
        ResultsAdapter resultsAdapter = new ResultsAdapter(this,
                results.toArray(new Result[results.size()]));
        listView.setAdapter(resultsAdapter);
    }

    /**
     * Set up a broadcast receiver to detect when the CommsService has received results.
     */
    private void setupBroadcastReceiver() {
        // we use a local broadcast receiver as broadcasts are staying within the app
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // this is the callback that runs when the results arrive
        resultsReceivedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // check that the results are the ones we want
                if(resultsCache.hasResults(requestUuid)) {
                    // display them
                    displayResults(resultsCache.getResults(requestUuid));
                }
            }
        };

        IntentFilter filter = new IntentFilter(CommsService.ACTION_RESULTS_RECEIVED);

        localBroadcastManager.registerReceiver(resultsReceivedBroadcastReceiver, filter);
    }
}
