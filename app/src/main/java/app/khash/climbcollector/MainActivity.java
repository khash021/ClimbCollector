package app.khash.climbcollector;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Khash on May.28.2018
 *
 * This is just the main activity that points to other acitivities for further operation
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MainActivity.class.getSimpleName();

    public final static int REQUEST_CODE = 1;

    private EditText mRouteNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get location permission
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);

            // REQUEST_CODE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } //permission

        Button collectButton = findViewById(R.id.bttn_collect_data);
        collectButton.setOnClickListener(this);

        Button mapViewButton = findViewById(R.id.bttn_view_data_map);
        mapViewButton.setOnClickListener(this);

        Button listViewData = findViewById(R.id.bttn_view_data_list);
        listViewData.setOnClickListener(this);

        mRouteNameText = findViewById(R.id.et_route_name);

    }//onCreate

    @Override
    public void onClick(View v) {
        Log.v(TAG, "onClicked called with id: " + getResources().getResourceName(v.getId()));

        int id = v.getId();

        switch (id) {
            case R.id.bttn_collect_data:
                String routeName = mRouteNameText.getText().toString();
                Log.v(TAG, "Route name: " + routeName);
                Intent collectIntent = new Intent(MainActivity.this, CollectDataActivity.class);
                collectIntent.putExtra(getString(R.string.route_name_intent_extra), routeName);
                startActivity(collectIntent);
                break;
            case R.id.bttn_view_data_map:
                Intent mapViewIntent = new Intent(MainActivity.this, MapViewActivity.class);
                startActivity(mapViewIntent);
                break;
            case R.id.bttn_view_data_list:
                Intent listViewIntent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(listViewIntent);
                break;
        }//switch
    }//onClick


}//MainActivity
