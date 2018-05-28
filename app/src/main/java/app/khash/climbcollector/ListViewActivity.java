package app.khash.climbcollector;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import app.khash.climbcollector.DataBase.DataContract.DataEntry;

/**
 * Created by Khash on May.28.2018
 *
 * Basic ListView activity showing the data with some options to modify/delete
 */

public class ListViewActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Integer loader constant; you can set it up as any unique integer
    private final static int LOCATION_LOADER = 0;

    //Adapter for our ListView
    DataCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        //find teh ListView and set it up
        ListView locationListView = findViewById(R.id.list_view);

        //Setup an adapter to create a list item for each row of location data in the Cursor.
        //There is no location data yet (until the loader finished)so pass in null for the Cursor
        mCursorAdapter = new DataCursorAdapter(this, null);

        //Attach the cursoradapter to listview
        locationListView.setAdapter(mCursorAdapter);

        //Kick off the loader
        getLoaderManager().initLoader(LOCATION_LOADER, null, this);

    }//onCreate

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection for the columns we want
        String[] projection = {
                DataEntry._ID,
                DataEntry.COLUMN_DATA_ROUTE_NAME,
                DataEntry.COLUMN_DATA_LATITUDE,
                DataEntry.COLUMN_DATA_LONGITUDE,
                DataEntry.COLUMN_DATA_ALTITUDE
        };

        /**
         * We can order the list using the sorOrder argument; null will use the default sort.
         * Formatted as SQL ORDER BY clause (excluding the ORDER BY itself.
         * for reference: ORDER BY <column_name> <ASC|DESC>
         *     Here I want it to be ordered in descending order based on the ID (newest first)
         */
        String sortOrder = DataEntry._ID + " DESC";

        //This loader will execute the ContentProvider's query method on background thread
        return new CursorLoader(this,   //parent activity context
                DataEntry.CONTENT_URI,           //provider content Uri to query
                projection,                     //The columns to return for each row
                null,                   //Selection criteria
                null,               //Selection criteria
                sortOrder                    //The sort order for returned rows
        );
    }//onCreateLoader

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update {@link LocationCursorAdapter} with new cursor containing updated location data
        mCursorAdapter.swapCursor(data);
    }//onLoadFinished

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }//onLoaderReset

}//main class
