package app.khash.climbcollector;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import app.khash.climbcollector.DataBase.DataContract.DataEntry;

/**
 * Created by Khashayar on 2/23/2018.
 *
 * {@link DataCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of data as its data source. This adapter knows
 * how to create list items for each row of location data in the {@link Cursor}.
 */

public class DataCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link DataCursorAdapter}.
     *
     * @param context       The context
     * @param cursor        The cursor from which to get the data.
     */
    public DataCursorAdapter (Context context, Cursor cursor) {
        super(context, cursor);
    }//LocationCursorAdapter


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }//newView


    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current data can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView idTextView = view.findViewById(R.id.data_id);
        TextView summaryTextView = view.findViewById(R.id.data_summary);

        /**
         *   Find the columns of location attributes that we're interested in our Cursor is already
         *   set at a specific row (managed by the listView depending on where we are so we don't need
         *   to worry about row index). However, in order to get the data from a specific column
         *   (here getting the ID, time, and other checkbox info), first we need to find the index
         *   associated with that column name. This is exactly what cursor.getColumnIndex does.
         *   It returns an integer which is the index of the Column name that was passed in as a
         *   String argument.
         */
        int idColumnIndex = cursor.getColumnIndex(DataEntry._ID);
        int routeNameColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_ROUTE_NAME);
        int latColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_LATITUDE);
        int lngColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_LONGITUDE);
        int altColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_ALTITUDE);

        /**
         *  Read the location attributes from the Cursor for the current location. Now that we have
         *  the column index, we can get the value associated with that column index by
         *  cursor.getString method and we pass in our index. It is possible to put
         *  cursor.getColumnIndex(DataEntry._ID) as the input argument of the getString method;
         *  I left it this way to understand better.
         *  We will also handle comments/establishment with onTouch popup message to conserve space
         *  in the listview
         */

        String dataId = cursor.getString(routeNameColumnIndex) + "-" + cursor.getString(idColumnIndex);
        double dataLat = cursor.getDouble(latColumnIndex);
        double dataLng = cursor.getDouble(lngColumnIndex);
        double dataAlt = cursor.getDouble(altColumnIndex);
        String summary = String.valueOf(dataLat) + ", " + String.valueOf(dataLng) + ", " +
                String.valueOf(dataAlt) + " m";

        // Populate fields with extracted properties
        idTextView.setText(dataId);

        //Create the summary and set the text
        summaryTextView.setText(summary);

    }//bindView
}//DataCursorAdapter
