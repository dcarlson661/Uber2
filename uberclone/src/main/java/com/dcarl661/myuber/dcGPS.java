
package com.dcarl661.myuber;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import static java.security.AccessController.getContext;

/**
 * Created by david carlson_2 on 9/20/2017.
 * //https://stackoverflow.com/questions/5783611/android-best-way-to-implement-locationlistener-across-multiple-activities
 */

public class dcGPS {

    private IGPSActivity main;

    // Helper for GPS-Position
    private LocationListener mlocListener;
    private LocationManager  mlocManager;
    private String           mProvider=LocationManager.GPS_PROVIDER;//"gps"; //"network"

    private boolean isRunning;

    public dcGPS(IGPSActivity main) {
        this.main = main;

        // GPS Position
        mlocManager  = (LocationManager) ((Activity) this.main).getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        try{
            mlocManager.requestLocationUpdates(mProvider, 2000, 0, mlocListener);
        }
        catch(SecurityException sex){
            Log.d("GPS", sex.getMessage());
        }
        // GPS Position END
        this.isRunning = true;
    }
    public void setProvider(String p){
        mProvider=p;
    }

    public void stopGPS() {
        if(isRunning) {
            mlocManager.removeUpdates(mlocListener);
            this.isRunning = false;
        }
    }

    public void resumeGPS()
    {
        if (ActivityCompat.checkSelfPermission((Activity)this.main, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText((Activity)main, "You don't have GPS permissions set.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity)main, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        else
        {
            mlocManager.requestLocationUpdates(mProvider, 1000, 0, mlocListener);
            this.isRunning = true;
        }
    }
    public void xresumeGPS() {
        if (ActivityCompat.checkSelfPermission((Activity)this.main,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission((Activity)main, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity)main, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        else
        {
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            this.isRunning = true;
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public class MyLocationListener implements LocationListener {

        private final String TAG = MyLocationListener.class.getSimpleName();

        @Override
        public void onLocationChanged(Location loc) {
            dcGPS.this.main.locationChanged(loc);//loc.getLongitude(), loc.getLatitude(),loc.getAltitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            dcGPS.this.main.displayGPSSettingsDialog();
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

}

