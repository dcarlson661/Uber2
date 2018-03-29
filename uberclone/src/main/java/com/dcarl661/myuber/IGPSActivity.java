package com.dcarl661.myuber;
import android.location.Location;

/**
 * Created by david carlson_2 on 9/20/2017.
 * * //https://stackoverflow.com/questions/5783611/android-best-way-to-implement-locationlistener-across-multiple-activities
 */

public interface IGPSActivity {
    public void locationChanged(Location loc);// longitude, double latitude, double altitude);
    public void displayGPSSettingsDialog();
    //public String  GetGPSProvider();
}

