package app.khash.climbcollector;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

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
//        RadioButton normalStyle = findViewById(R.id.radio_normal);
//        RadioButton satelliteStyle = findViewById(R.id.radio_sat);
//        RadioButton terrainStyle = findViewById(R.id.radio_terr);
//        RadioButton hybridStyle = findViewById(R.id.radio_hybrid);

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



    }//onMapReady
}
