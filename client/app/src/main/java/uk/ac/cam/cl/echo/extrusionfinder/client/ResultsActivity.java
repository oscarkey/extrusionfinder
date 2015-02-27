package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.List;

import uk.ac.cam.cl.groupecho.extrusionfinder.R;

public class ResultsActivity extends ActionBarActivity {

    private static final String LOG_TAG = "ResultsActivity";

    public static final String EXTRA_REQUEST_UUID
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.REQUEST_UUID";
    public static final String EXTRA_PART_ID
            = "uk.ac.cam.cl.echo.extrusionfinder.client.extra.EXTRA_PART_ID";

    private Context context;
    private String requestUuid;
    private ResultsCache resultsCache;
    private BroadcastReceiver broadcastReceiver;
    private GridView gridView;

    /**
     * Send an intent to start the ResultsActivity
     * @param context a context
     * @param uuid the uuid of the request that we wish to display results for
     */
    public static void startWithUuid(Context context, String uuid) {
        Intent intent = new Intent(context, ResultsActivity.class);
        intent.putExtra(EXTRA_REQUEST_UUID, uuid);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        context = this;

        // get a reference to the list view
        gridView = (GridView) findViewById(R.id.resultsGridView);

        // initially show a progress bar/spinner
        ProgressBar progressSpinner = (ProgressBar) findViewById(R.id.resultsProgressBar);
        gridView.setEmptyView(progressSpinner);

        // add an event listener to the list to detect when a result is clicked on
        gridView.setOnItemClickListener(onItemClickListener);

        // set up a broadcast receiver so we are notified when new results arrive
        setupBroadcastReceiver();

        // get the request id from the intent
        requestUuid = getIntent().getStringExtra(EXTRA_REQUEST_UUID);

        // check the cache to see if we have already have data for this request
        resultsCache = ResultsCache.getInstance(this);
        if(!resultsCache.hasResults(requestUuid)) {
            // launch the service to request the results
            // these are stored in the cache and then we get a broadcast
            CommsService.startActionRequestResults(this, requestUuid);
        }
        // results are displayed on resume
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check to see if we have results to displayed, might have arrived during pause
        List<Result> results = resultsCache.getResults(requestUuid);
        if(results != null) {
            displayResults(results);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        resultsCache.close();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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
        gridView.setAdapter(resultsAdapter);
    }

    /**
     * Indicate an error to the user using a dialog
     * @param errorType the type as given in the constants in CommsServer
     */
    private void displayError(int errorType) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        // set the title and text of the dialog appropriately for the error
        switch(errorType) {
            case CommsService.ERROR_TYPE_NETWORK:
                dialogBuilder.setTitle(R.string.title_dialog_error_no_network);
                dialogBuilder.setMessage(R.string.message_dialog_error_no_network);
                break;
            case CommsService.ERROR_TYPE_OTHER:
                dialogBuilder.setTitle(R.string.title_dialog_error_other);
                dialogBuilder.setMessage(R.string.message_dialog_error_other);
                break;
            default:
                throw new IllegalArgumentException("Unknown error type: " + errorType);
        }

        // add the ok button
        //TODO allow retry
        dialogBuilder.setNeutralButton(R.string.text_button_neutral, null);

        // detect the dialog being closed for some reason, probably by pressing ok
        // this has to be added to the dialog and not the builder to support older devices
        AlertDialog dialog = dialogBuilder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // move back to the image capture activity
                Intent intent = new Intent(context, ImageCaptureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });

        dialog.show();
    }

    /**
     * Set up a broadcast receiver to detect when the CommsService has received results.
     */
    private void setupBroadcastReceiver() {
        // we use a local broadcast receiver as broadcasts are staying within the app
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // callback than runs when broadcasts arrive
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // determine the action of the intent
                switch(intent.getAction()) {
                    case CommsService.ACTION_RESULTS_RECEIVED: {
                        // check that the results are the ones we want
                        if(resultsCache.hasResults(requestUuid)) {
                            // display them
                            displayResults(resultsCache.getResults(requestUuid));
                        }
                        break;
                    }
                    case CommsService.ACTION_REQUEST_ERROR: {
                        // check that this error is about a request we care about
                        String uuid = intent.getStringExtra(CommsService.EXTRA_REQUEST_UUID);
                        if(uuid.equals(requestUuid)) {
                            int errorType = intent.getIntExtra(
                                    CommsService.EXTRA_REQUEST_ERROR_TYPE, -1);
                            displayError(errorType);
                        }
                        break;
                    }
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(CommsService.ACTION_RESULTS_RECEIVED);
        filter.addAction(CommsService.ACTION_REQUEST_ERROR);

        localBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    private AdapterView.OnItemClickListener onItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // get the id of the part
            String partId = ((ResultsAdapter) gridView.getAdapter()).getPartId(position);

            // launch the detail activity
            Intent intent = new Intent(context, PartDetailActivity.class);
            intent.putExtra(EXTRA_REQUEST_UUID, requestUuid);
            intent.putExtra(EXTRA_PART_ID, partId);
            context.startActivity(intent);
        }
    };
}
