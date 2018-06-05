package app.khash.climbcollector;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.khash.climbcollector.DataBase.DataContract.DataEntry;

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

        Button deleteButton = findViewById(R.id.bttn_delete_all);
        deleteButton.setOnClickListener(this);

        Button startServiceButton = findViewById(R.id.bttn_service_start);
        startServiceButton.setOnClickListener(this);

        Button stopServiceButton = findViewById(R.id.bttn_service_stop);
        stopServiceButton.setOnClickListener(this);

    }//onCreate

    @Override
    public void onClick(View v) {
        Log.v(TAG, "onClicked called with id: " + getResources().getResourceName(v.getId()));

        int id = v.getId();

        switch (id) {
            case R.id.bttn_collect_data:
                String routeName = mRouteNameText.getText().toString().trim();
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
            case R.id.bttn_delete_all:
//                confirmDelete();
                Toast.makeText(this, "Disabled", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bttn_service_start:
                Intent serviceStartIntent = new Intent(this, GPSService.class);
                String routeName2 = mRouteNameText.getText().toString().trim();
                if (routeName2.trim().length() < 1) {
                    //no name input
                    routeName2 = "NA";
                }
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String key = getString(R.string.route_name_intent_extra);
                sharedPref.edit().putString(key, routeName2).apply();
                startService(serviceStartIntent);
                Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bttn_service_stop:
                Intent serviceStopIntent = new Intent(this, GPSService.class);
                stopService(serviceStopIntent);
                Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
                break;
        }//switch
    }//onClick

    /**
     * Helper method to deal with the confirmation of the action delete all.
     *
     * It first creates a simple AlerDialog. Then it launches the Dialog warning the user
     * that this action will not be reversible and ask wither to confirm or cancel
     */
    //TODO: Create another class for making all these dialogs so we can just refer to it once.
    private void confirmDelete() {
        /**
         * We first make an object of AlertDialog.Builder class (builder). This is used to make the
         * dialog. We set the title, and action of each button with this object.
         * Then we will create an object of AlertDialog class (dialogConfirmation) which will
         * actually show the dialog. We do this using create() method on the build object.
         * Then we show the dialog by calling the show() method on the dialog object.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CAUTION").setMessage("This will delete the entire database, " +
                "and is irreversible.\nWould you like to continue?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //By passing in 1 as the whereClause, it will delete all rows and return the number
                //of rows deleted.
                String whereClause = "1";
                int result = getContentResolver().delete(DataEntry.CONTENT_URI,
                        whereClause, null);
                //We should be getting an integer with the number of deleted rows since we have
                // passed in 1 as where clause
                Toast.makeText(MainActivity.this,
                        "All rows in database have been deleted" +
                                "\nnumber of deleted rows: " + result,
                        Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialogConfirmation = builder.create();
        dialogConfirmation.show();
    }//confirmDelete


}//MainActivity
