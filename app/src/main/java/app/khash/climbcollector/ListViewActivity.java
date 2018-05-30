package app.khash.climbcollector;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

        //set an empty view
        View emptyView = findViewById(R.id.empty_view);
        locationListView.setEmptyView(emptyView);

        //set up on long click listener to delete the data point
        locationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //id is the databse id
                //pass in the id to the confirmation dialog
                showDeleteDialog((int) id);
                return false;
            }
        });//onLongClick

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

    private void showDeleteDialog(int id) {
        final int itemId = id;
        //Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder deleteConfirmation = new AlertDialog.Builder(this);
        //Chain together various setter methods to set the dialogConfirmation characteristics
        deleteConfirmation
                .setMessage("Are you sure you want to delete this item?")
                .setTitle("CAUTION");
        // Add the buttons. We can call helper methods from inside the onClick if we need to
        deleteConfirmation.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //delete the item in databse
                deleteItem(itemId);
            }
        });
        deleteConfirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /**
         * Now we need to make our AlerDialog object (this is the one that will actually show the
         * dialog.
         * We do this by creating an object of AlertDialog class and use create() method on our
         * builder object which we set up above, to create the AlertDialog object.
         * Then we can call show() method on the AlertDialog object whenever we want to show that
         */

        final AlertDialog dialogConfirmation = deleteConfirmation.create();

        dialogConfirmation.show();

    }//showDeleteDialog

    //helper method for deleting a single data item in db
    private void deleteItem(int id) {
        //create the URI
        Uri itemUri = ContentUris.withAppendedId(DataEntry.CONTENT_URI, id);
        //send it to the content provider
        int result = getContentResolver().delete(itemUri, null , null);
        //Check to see if the delete was successful
        if (result == 1) {
            Toast.makeText(getApplicationContext(),
                    "Data (" + id +") deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Error deleting", Toast.LENGTH_SHORT).show();
        }

    }//deleteItem

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
