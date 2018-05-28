package app.khash.climbcollector.DataBase;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Khash
 *
 *  * This is the class that contains the schema of the table and all the constants for out DB
 */

public class DataContract {

    //Default empty constructor
    private DataContract(){}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     *
     * This has to math android:authorities that was defined in the Provider part of Manifest
     */
    public static final String CONTENT_AUTHORITY = "app.khash.climbcollector";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://app.khash.climbcollector.DataBase/data/ is a valid path for
     * looking at location data. content://com.example.android.locations/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_LOCATIONS = "data";



    // Inner class that defines the table contents
    public static class DataEntry implements BaseColumns {

        /** The content URI to access the location data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LOCATIONS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of locations.
         */
        //we’re making use of the constants defined in the ContentResolver class:
        //CURSOR_DIR_BASE_TYPE (which maps to the constant "vnd.android.cursor.dir") and
        // CURSOR_ITEM_BASE_TYPE (which maps to the constant “vnd.android.cursor.item”)
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATIONS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single location.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATIONS;

        /*
                Note that all of these Strings are the object data type of those names;
                and not the actual data type of the database in the corresponding column.
         */

        public static final String TABLE_NAME = "data";


        /**
         * Unique ID number for the location (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        /**
         * Latitude of the datapoint
         *
         * Type: REAL
         */
        public final static String COLUMN_DATA_LATITUDE ="latitude";


        /**
         * Longitude of datapoint
         *
         * Type: REAL
         */
        public final static String COLUMN_DATA_LONGITUDE = "longitude";


        /**
         * Altitude of datapoint
         *
         * Type: REAL
         */
        public final static String COLUMN_DATA_ALTITUDE ="altitude";


        /**
         * Route Name
         *
         * Type: TEXT
         */
        public final static String COLUMN_DATA_ROUTE_NAME = "route_name";


        /**
         * Date
         *
         * the format = MM.dd.yyyy at hh:mm z (e.g. 02.28.2018 at 02:50 PST)
         *
         * Type: TEXT
         */
        public final static String COLUMN_DATA_DATE = "date_time";

    }//DataEntry

}//DataContract
