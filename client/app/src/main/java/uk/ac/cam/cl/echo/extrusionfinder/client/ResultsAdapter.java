package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import uk.ac.cam.cl.groupecho.extrusionfinder.R;

/**
 * Created by oscar on 10/02/15.
 * Subclass of ArrayAdapter for connecting an array of Results to a list view
 */
public class ResultsAdapter extends ArrayAdapter<Result> {
    private Result[] results;
    private final LayoutInflater layoutInflater;

    public ResultsAdapter(Context context, Result[] results) {
        super(context, R.layout.list_item_results, results);

        this.results = results;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Get the view for the list item at the given position
     * @param position Position in the list
     * @param convertView View to use, or null if one needs to be created
     * @param parent
     * @return The view to be displayed in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if the convert view is null we need to create a view
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_results, null);
        }

        Result result = results[position];

        // set the image
        ImageView imageView = (ImageView)convertView;
        if(result.hasImageLink()) {
            // use picasso to display the image
            Picasso.with(getContext())
                    .load(result.getImageLink())
                    .placeholder(R.drawable.blank)
                    .into(imageView);
        }
        else {
            // use a placeholder
            imageView.setImageResource(R.drawable.blank);
        }

        return convertView;
    }

    /**
     * Get the string id of a part at a given position in the list
     * @param position The position of the part in the result list
     * @return The String id of the part given by Result.getId()
     */
    public String getPartId(int position) {
        return results[position].getId();
    }
}
