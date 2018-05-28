package app.khash.climbcollector.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import app.khash.climbcollector.DataBase.DataContract.DataEntry;

/**
 * Created by Khash
 *
 * This is the class that creates the database.
 */

public class DataDBHelper extends SQLiteOpenHelper {

    public static final String TAG = DataDBHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "data.db";


    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    /**
     * Constructs a new instance of {@link DataDBHelper}.
     *
     * @param context of the app
     */
    public DataDBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }//LocationDbHelper


    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate() called");
        // Create a String that contains the SQL statement to create the locations table
        /**
         *  Remember the SQLite command:
         CREATE TABLE <table name> (<column_name> <column_datatype>, .....)
         We are doing the same thing except we make it a String constant so we can just pass in the
         String to the method (execSQL) instead of writing it everytime
         */


        String SQL_CREATE_DATA_TABLE = "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                DataEntry._ID + " INTEGER PRIMARY KEY, " +
                DataEntry.COLUMN_DATA_LATITUDE + " REAL NOT NULL, " +
                DataEntry.COLUMN_DATA_LONGITUDE + " REAL NOT NULL, " +
                DataEntry.COLUMN_DATA_ALTITUDE + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_DATA_ROUTE_NAME + " TEXT, " +
                DataEntry.COLUMN_DATA_DATE + " TEXT);";

        /**
         *    Execute the SQL statement
         *    Note; execSQL is not a static method and we run that on the SQLiteDatabase object (db)
         *    that was passed in as an input argument in the onCreate method
         */
        db.execSQL(SQL_CREATE_DATA_TABLE);
        Log.d(TAG, "Table Created");
    }//onCreate

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }//onUpgrade

}//DataDBHelper
