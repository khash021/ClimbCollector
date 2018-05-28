package app.khash.climbcollector;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import app.khash.climbcollector.DataBase.DataContract.DataEntry;

/**
 * Created by Khash on May.28.2018
 *
 * This is the main activity for collecting the data periodically and save it to the database
 */

public class CollectDataActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = CollectDataActivity.class.getSimpleName();

    GoogleApiClient mGoogleApiClient;

    final static DateFormat mDateFormat = new SimpleDateFormat("MM.dd.yyyy 'at' HH:mm:ss z");

    private TextView mLatText, mLngText, mAltText;

    private String mRouteName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        mRouteName = getIntent().getStringExtra(getString(R.string.route_name_intent_extra));

        //build api client
        buildGoogleApiClient();

        //find textviews
        mLatText = findViewById(R.id.text_lat);
        mLngText = findViewById(R.id.text_lng);
        mAltText = findViewById(R.id.text_alt);



    }//onCreate

    @Override
    public void onLocationChanged(Location location) {

        //get the location data
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        String alt = String.valueOf(location.getAltitude());

        //show the data
        mLatText.setText(lat);
        mLngText.setText(lng);
        mAltText.setText(alt);

        insertDataToDb(location);

    }//onLocationChanged


    //method for adding the location data to db
    private void insertDataToDb(Location location) {

        //Current date and time using the format declared at the beginning
        final String currentDateTime = mDateFormat.format(Calendar.getInstance().getTime());

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double alt = location.getAltitude();

        // Create a new map of values,
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_DATA_LATITUDE, lat);
        values.put(DataEntry.COLUMN_DATA_LONGITUDE, lng);
        values.put(DataEntry.COLUMN_DATA_ALTITUDE, alt);
        values.put(DataEntry.COLUMN_DATA_DATE, currentDateTime);
        values.put(DataEntry.COLUMN_DATA_ROUTE_NAME, mRouteName);

        // Insert a new location into the provider, returning the content URI for the new location.
        Uri newUri = getContentResolver().insert(DataEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Log.v(TAG, "error saving data");
        } else {
            //since the insert method return the Uri of the row created, we can extract the ID of
            //the new row using the parseID method with our newUri as an input. This method gets the
            //last segment of the Uri, which is our new ID in this case and we store it in an object
            // And add it to the confirmation method.
            String id = String.valueOf(ContentUris.parseId(newUri));
            // Otherwise, the insertion was successful and we can log
            Log.v(TAG, "Successfully added: " + id);
        }

    }//insertDataToDb

    protected synchronized void buildGoogleApiClient(){
        //Building a GoogleApiClient on
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //This tells it we want to use the LocationServices API
                .addApi(LocationServices.API)
                //Means we want the ConnectionCallbacks on the GoogleApiClient client to come to "this" class
                .addConnectionCallbacks(this)
                //Same as above, send the callbacks to this activity. this could be replaced by any other activity
                .addOnConnectionFailedListener(this)
                //Finally build the ApiClient
                .build();
    }//buildGoogleApiClient

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        //LocationRequest object
        LocationRequest mLocationRequest;
        //Create a LocationRequest using create() method
        mLocationRequest = LocationRequest.create();
        //set the interval on the locationRequest object (times are in milli seconds).
        // This is how often it updates
        mLocationRequest.setInterval(1000);
        //set the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Log.d(TAG, "mLocationRequest: " + mLocationRequest);

        /**
         *  We could just simply put the line LocationServices.Fused.... here; but this way, we
         *  first make sure that we have permission, and then we can start requesting our location
         *  updates.
         */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.REQUEST_CODE);
        } else {
            //Start requesting location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }//onConnected

    @Override
    public void onConnectionSuspended(int i) {

    }//onConnectionSuspended

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }//onConnectionFailed

    @Override
    protected void onStart() {

        super.onStart();
        //Connect client
        mGoogleApiClient.connect();
    } //onStart

    @Override
    protected void onStop() {

        //Disconnect the client
        mGoogleApiClient.disconnect();
        super.onStop();
    } //onStop

    @Override
    protected void onPause() {

        //Disconnect the client on pause
        mGoogleApiClient.disconnect();
        super.onPause();
    } //onPause

    @Override
    protected void onResume() {

        //Re-connect the client on resume
        mGoogleApiClient.connect();
        super.onResume();
    } //onResume


}//class
