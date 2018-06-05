package app.khash.climbcollector;

import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import app.khash.climbcollector.DataBase.DataContract.DataEntry;

//TODO: CLEAN UP, GO THROUGH THE CODE AND CHANGE VARIABLE NAMES AND COMMENT

public class GPSService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = GPSService.class.getSimpleName();

    //TODO: write description for the methods

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
    }//onCreate


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        return START_STICKY;
    }//onStartCommand


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdate();
    }//onConnected

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended " + i);

    }//onConnectionSuspended

    @Override
    public void onLocationChanged(Location location) {

        //insert location to the database passing in the location object
        insertDataToDb(location);
    }//onLocationChanged

    //method for adding the location data to db
    private void insertDataToDb(Location location) {

        final DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy 'at' HH:mm:ss z");

        //Current date and time using the format declared at the beginning
        final String currentDateTime = dateFormat.format(Calendar.getInstance().getTime());

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double alt = location.getAltitude();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.route_name_intent_extra);
        String routeName = sharedPref.getString(key, "default");

        // Create a new map of values,
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_DATA_LATITUDE, lat);
        values.put(DataEntry.COLUMN_DATA_LONGITUDE, lng);
        values.put(DataEntry.COLUMN_DATA_ALTITUDE, alt);
        values.put(DataEntry.COLUMN_DATA_DATE, currentDateTime);
        values.put(DataEntry.COLUMN_DATA_ROUTE_NAME, routeName);


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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed ");

    }//onConnectionFailed

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }//initLocationRequest

    private void startLocationUpdate() {
        initLocationRequest();

        //check for location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }//check permission

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }//startLocationUpdate

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }//buildGoogleApiClient

    @Override
    public void onDestroy() {
        super.onDestroy();

        mGoogleApiClient.disconnect();
    }//onDestroy


}//GPSService
