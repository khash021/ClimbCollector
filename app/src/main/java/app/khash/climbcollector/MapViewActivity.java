package app.khash.climbcollector;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import app.khash.climbcollector.DataBase.DataContract.DataEntry;

/**
 * Created by Khash on May.28.2018
 *
 * Simple activity, showing a map with the collected data
 */

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        RadioGroup mapStyleGroup = findViewById(R.id.radio_group_style);
        mapStyleGroup.check(R.id.radio_normal);
        mapStyleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_normal:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.radio_sat:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.radio_terr:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case R.id.radio_hybrid:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                }//switch
            }
        });//mapStyleGroup

    }//onCreate

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        //check for permission and enable my location button
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        } //permission


        populateMap();

    }//onMapReady

    private void populateMap() {

        //this is what we are going to pass into the Query method. This String is similar
        //to the statement after SELECT, we tell it which columns we want, here we want everything
        String[] projection = {
                DataEntry._ID,
                DataEntry.COLUMN_DATA_LATITUDE,
                DataEntry.COLUMN_DATA_LONGITUDE,
                DataEntry.COLUMN_DATA_ALTITUDE,
                DataEntry.COLUMN_DATA_ROUTE_NAME
        };

        Cursor cursor = getContentResolver().query(
                DataEntry.CONTENT_URI,     //The content Uri
                projection,               //The columns to return for each row
                null,            //Selection criteria
                null,         //Selection criteria
                null            //The sort order for returned rows
        );
        //get the column id of the table
        int idColumnIndex = cursor.getColumnIndex(DataEntry._ID);
        int latColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_LATITUDE);
        int lngColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_LONGITUDE);
        int altColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_ALTITUDE);
        int routeNameColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_ROUTE_NAME);

        try {
            Marker marker;
            while (cursor.moveToNext()) {
                String title = cursor.getString(routeNameColumnIndex) + "-" + cursor.getString(idColumnIndex);
                double lat = cursor.getDouble(latColumnIndex);
                double lng = cursor.getDouble(lngColumnIndex);
                LatLng latLng = new LatLng(lat, lng);
                double alt = cursor.getDouble(altColumnIndex);

                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(String.valueOf(alt))
                );
            }//while
        } finally {

            //close the cursor
            cursor.close();
            //Return the ArrayList
        }//try-finally
    }//populateMap


}//main class
