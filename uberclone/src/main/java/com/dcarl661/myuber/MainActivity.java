package com.dcarl661.myuber;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Locale;

public class MainActivity
        extends AppCompatActivity
        implements IGPSActivity
{

    Switch aSwitch;
    Button button;

    public dcGPS gps                     = null;
    private volatile Location blackboard = null;
    public int bkCount                   = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view)
            {
                //ToDo test then remove
                ParseObject object   = new ParseObject("ExampleObject");
                object.put             ("myNumber", "456");
                object.put             ("myString", "dave");
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException ex) {
                        if (ex == null)
                        {
                            Log.i("Parse Result", "Successful!");
                            Snackbar.make(view, "Successful parse save.", Snackbar.LENGTH_SHORT).setAction("Successful", null).show();
                        }
                        else
                        {
                            Log.i("Parse Result", "Failed" + ex.toString());
                            Snackbar.make(view, "Failed Parse", Snackbar.LENGTH_SHORT).setAction("Failed", null).show();
                        }
                    }
                });
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });


//        Parse.enableLocalDatastore(this);
//        // Add your initialization code here
//        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
//                .applicationId("d3f31d2d4d68a6908ecee1892314a90b7d9a00e7")
//                .clientKey("ad7d23c82478f30d7f96dd738e60bc1a7314e082")
//                .server("http://ec2-13-57-220-78.us-west-1.compute.amazonaws.com/parse/")
//                .build()
//        );
//        //ParseUser.enableAutomaticUser();
//        ParseACL defaultACL = new ParseACL();
//        defaultACL.setPublicReadAccess(true);
//        defaultACL.setPublicWriteAccess(true);
//        ParseACL.setDefaultACL(defaultACL, true);

        ////////////////Put all my stuff here

        button=findViewById(R.id.button);
        aSwitch=findViewById(R.id.userTypeSwitch);

        ParseUser user=ParseUser.getCurrentUser();
        if(user==null){
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e==null){
                        Log.i("info", "Anonymous login successful");
                    }
                    else{
                        Log.i("info", "Anonymous login Failed");
                    }
                }
            });
        }
        else
        {
            if(user.get("riderOrDriver") != null){
                Log.i("info", "redirecting as " + user.get("riderOrDriver"));
                redirectActivity();
            }
        }

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        gps                           = new dcGPS(this);
        boolean useNetworkGPSProvider = true;  //normally a checkbox and a saved preference
        if(useNetworkGPSProvider){//.isChecked()){
            gps.setProvider("network");
        }
        else{
            gps.setProvider("gps");
        }

    }
    public void startGPS()
    {
        if(null!=gps){
            if(!gps.isRunning()) gps.resumeGPS();
            blackboard=null;
        }
        else
        {
            gps=new dcGPS(this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    gps.resumeGPS();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null!=gps){
            gps.stopGPS();
            blackboard=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startGPS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(this.findViewById(android.R.id.content).getRootView(),
                    "Settings coming soon.", Snackbar.LENGTH_SHORT)
                    .setAction("settings", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //the button click
    public void getStarted(View v)
    {
        Log.i("info", String.valueOf(aSwitch.isChecked()));
        String userType="rider";
        if(aSwitch.isChecked()){
            userType="driver";
        }
        ParseUser.getCurrentUser().put("riderOrDriver",userType);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                redirectActivity();;
            }
        });
    }

    public void redirectActivity()
    {
        if(ParseUser.getCurrentUser().get("riderOrDriver").equals("rider"))
        {
            Intent intent = new Intent(getApplicationContext(),RiderActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent=new Intent(getApplicationContext(),ViewRequestsActivity.class);
            startActivity(intent);
        }
    }

    public void locationChanged(Location loc)
    {
        //called by dcGPS.java
        this.blackboard=loc;
        bkCount++;
        Log.d("gpsInfo", "Main-Longitude: " + loc.getLongitude());
        Log.d("gpsInfo", "Main-Latitude: "  + loc.getLatitude());
        Log.d("gpsInfo", "Main-Provider: "  + loc.getProvider());
        Log.d("gpsInfo", "Main-Accuracy: "  + loc.getAccuracy());
        if(null!=blackboard)
        {
            String sLat =Double.toString(blackboard.getLatitude());
            String sLon =Double.toString(blackboard.getLongitude());
            String fs   =String.format    ("locChg %d,%s,%s %s",
                    bkCount,sLat,sLon,blackboard.getProvider()  );
            //ToDo figure out how to update this UIThread thing gpsList.add(0,fs);
            //TextViewSeeGPSInfo.setText(fs);
        }
        //20180220 TESTing from android c2 course
        boolean geocode=true; //turn off after test
        if(geocode)
        {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(blackboard.getLatitude(),blackboard.getLongitude(),1);
                if (listAddresses != null && listAddresses.size() > 0) {
                    String address = "";
                    if (listAddresses.get(0).getThoroughfare() != null) {
                        address += listAddresses.get(0).getThoroughfare() + " ";
                    }
                    if (listAddresses.get(0).getLocality() != null) {
                        address += listAddresses.get(0).getLocality() + " ";
                    }
                    if (listAddresses.get(0).getPostalCode() != null) {
                        address += listAddresses.get(0).getPostalCode() + " ";
                    }
                    if (listAddresses.get(0).getAdminArea() != null) {
                        address += listAddresses.get(0).getAdminArea();
                    }
                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
                    Log.i("Address", address);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }//if test geocode
    }

    @Override
    public void displayGPSSettingsDialog() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        Toast.makeText(this, "displayGPSDialog Interface called.", Toast.LENGTH_SHORT).show();
    }
}
