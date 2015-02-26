package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.ac.cam.cl.groupecho.extrusionfinder.R;

public class PartDetailActivity extends ActionBarActivity {

    private static final String LOG_TAG = "PartDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_detail);

        // get the request id from the intent
        String uuid = getIntent().getStringExtra(ResultsActivity.EXTRA_REQUEST_UUID);
        String partId = getIntent().getStringExtra(ResultsActivity.EXTRA_PART_ID);
        Result result = getResult(uuid, partId);

        if(result == null) {
            // give up
            finish();
        }

        // set the user interface fields
        TextView nameText = (TextView) findViewById(R.id.partDetailName);
        nameText.setText(result.getId());

        // check if we have an image link
        if(result.hasImageLink()) {
            // load the image using Picasso
            ImageView imageView = (ImageView) findViewById(R.id.partDetailImage);
            Picasso.with(this)
                    .load(result.getImageLink())
                    .placeholder(R.drawable.blank)
                    .into(imageView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Result getResult(String uuid, String partId) {
        ResultsCache resultsCache = ResultsCache.getInstance(this);

        // get the results
        List<Result> results = resultsCache.getResults(uuid);
        if(results == null) {
            // something has gone wrong, give up
            //TODO handle this more gracefully
            Log.e(LOG_TAG, "ResultsCache does not contain results for uuid: " + uuid);
            return null;
        }

        // find the right one
        Result targetResult = null;
        for(Result result : results) {
            if(result.getId().equals(partId)) {
                targetResult = result;
                break;
            }
        }

        // if we didn't find a result then something odd is going on
        if(targetResult == null) {
            Log.e(LOG_TAG, "Result set did not contain an expected part: " + partId);
            return null;
        }

        resultsCache.close();

        return targetResult;
    }
}
