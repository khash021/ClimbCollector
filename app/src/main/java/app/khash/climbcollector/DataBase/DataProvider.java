package app.khash.climbcollector.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationProvider;
import android.net.Uri;
import android.util.Log;

import app.khash.climbcollector.DataBase.DataContract.DataEntry;

/**
 * Created by Khash
 */

/**
 * {@link ContentProvider} for Data Collection app.
 */
public class DataProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String TAG = LocationProvider.class.getSimpleName();

    //Database helper object
    DataDBHelper mDbHelper;

    /** URI matcher code for the content URI for the data table */
    private static final int DATA = 100;

    /** URI matcher code for the content URI for a single location in the locations table */
    private static final int DATA_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_LOCATIONS, DATA);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_LOCATIONS + "/#", DATA_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

        //We used getContext() here, because we cannot use 'this' can only be used when the Class
        //extends from Context (such as Application, Activity, Service and IntentServices
        mDbHelper = new DataDBHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        //Access database using the mDbHelper variable that we initialized in the onCreate, and get
        //the SQL object from the DbHelper
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        //Use the Uri matcher to help us determine what kind of input Uri was passed into us.
        //We will call match method on sUriMatcher (assigned in the beginning) and pass in the
        //uri that was given to us as an input argument of query. This will return an integer code
        int match = sUriMatcher.match(uri);

        //This is where we decide which path to go down based on the integer code of the Uri
        switch (match) {
            case DATA:
                //For the LOCATIONS code, query the locations table directly with the given input arguments.
                //This is because this code means the entire table and we do not need to decode the
                //specific row like we did in the DATA_ID case below where we inserted the desired
                //row to the selection, and selection arg.
                cursor = database.query(DataEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case DATA_ID:

                // For the DATA_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.locations/locations/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = DataEntry._ID + "=?";
                //ContentUris.parseID method converts the last path segment to a long (i.e. we get
                //the number after / corresponding to the row of the table
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the locations table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(DataEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                //This will make the app crash in case we have entered a wrong uri
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }//switch

        /**
         *    Set notification URI on the Cursor, so we know what content URI the Cursor was
         *    created for. If the data at this URI changes, then we know we need to
         *    update the Cursor.
         */

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }//query

    /**
     * Insert new data into the provider with the given ContentValues.
     */

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DATA:
                return insertData(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }//switch
    }//insert

    /**
     * Insert a data point into the database with the given content values. Return the new
     * content URI for that specific row in the database.
     */
    private Uri insertData(Uri uri, ContentValues values){
        //Usually we would perform sanity check here, in order to prevent wrong data to be added
        //to our database. However, since the location data comes directly from LocationServices,
        //and the rest are booleans from checkboxes; there is no need for sanity check. Moreover,
        //comment could be anything String, so we do not perfomr any sanity check here.


        //Access database using the mDbHelper variable that we initialized in the onCreate, and get
        //the SQL object from the DbHelper
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //We store the number that was returned from our insert method (i.e. column ID)
        long newRowId = database.insert(DataEntry.TABLE_NAME,null, values);

        if (newRowId == -1){
            //insert return -1 if there was an error and this means the location was NOT added
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        } //if

        // Notify all listeners that the data has changed for the location content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);

    } //insertData

    /**
     * Update location in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more locations).
     * Return the number of rows that were successfully updated.
     *
     * Location at this point cannot be updated from here, since it was automatically added using
     * the gps.
     *
     * This is mainly for updating checkboxes, and comments.
     */
    public int update( Uri uri, ContentValues values,  String selection,  String[] selectionArgs) {

        /**
         *  Since this is update, not all the values might be present. Only the ones that are going
         *  to be changed are passed in the ContentValues. We need to use the containsKey method on
         *  ContentValues. This return true if thr object has a value for tha name that was passed
         *  in, and we can update it then.
         */

        //We need to first extract the ID of the location we are updating.
        selection = DataEntry._ID + "=?";
        selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

        //Access database using the mDbHelper variable that we initialized in the onCreate, and get
        //the SQL object from the DbHelper
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Update method returns an int, corresponding to the number of rows that was affected
        int rowsUpdated  = database.update(DataEntry.TABLE_NAME, values,selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            // Notify all listeners that the data has changed for the location content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }//if

        return rowsUpdated ;
    }//update


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case DATA:
                rowsDeleted = database.delete(DataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DATA_ID:
                selection = DataEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(DataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }//switch
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            //// Notify all listeners that the data has changed for the location content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }//if
        return rowsDeleted;
    }//delete

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DATA:
                return DataEntry.CONTENT_LIST_TYPE;
            case DATA_ID:
                return DataEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }//getType

}//main
