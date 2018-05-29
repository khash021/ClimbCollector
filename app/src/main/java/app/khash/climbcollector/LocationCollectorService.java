//package app.khash.climbcollector;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//public class LocationCollectorService extends Service implements {
//
//    private final String TAG = LocationCollectorService.class.getSimpleName();
//
//    private LocationManager mLocationManager = null;
//    private static final int LOCATION_INTERVAL = 1000; //1 second
//    private static final float LOCATION_DISTANCE = 0;
//
//    private class LocationCollector implements LocationListener {
//
//        Location mLastLocation;
//
//        public LocationCollector (String provider) {
//            mLastLocation = new Location (provider);
//        }//LocationCollector
//
//        @Override
//        public void onLocationChanged(Location location) {
//            mLastLocation.set(location);
//
//        }//onLocationChanged
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }//onStatusChanged
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }//onProviderEnabled
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }//onProviderDisabled
//
//    }//LocationCollector-class
//
//    LocationListener[] mLocationListeners = new LocationListener[] {
//            new LocationListener(LocationManager.GPS_PROVIDER),
//            new LocationListener(LocationManager.NETWORK_PROVIDER)
//    };
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }//onBind
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
//    }//onStartCommand
//
//    @Override
//    public void onCreate() {
//        initializeLocationManager();
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[0]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//        }
//    }//onCreate
//
//    @Override
//    public void onDestroy() {
//
//        super.onDestroy();
//
//        if (mLocationManager != null) {
//            for (int i = 0; i < mLocationListeners.length; i++) {
//                try {
//                    mLocationManager.removeUpdates(mLocationListeners[i]);
//                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex);
//                }//try-catch
//            }//for
//        }//if
//    }//onDestroy
//
//
//    private void initializeLocationManager() {
//
//        if (mLocationManager == null) {
//            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        }//if
//    }//initializeLocationManager
//
//
//}//LocationCollectorService-service
