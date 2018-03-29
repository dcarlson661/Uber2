package com.dcarl661.myuber;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {

    Switch aSwitch;
    Button button;

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


        Parse.enableLocalDatastore(this);
        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("d3f31d2d4d68a6908ecee1892314a90b7d9a00e7")
                .clientKey("ad7d23c82478f30d7f96dd738e60bc1a7314e082")
                .server("http://ec2-13-57-220-78.us-west-1.compute.amazonaws.com/parse/")
                .build()
        );
        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

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
            }
        }

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


    public void getStarted(View v)
    {
        Log.i("info", String.valueOf(aSwitch.isChecked()));
        String userType="rider";
        if(aSwitch.isChecked()){
            userType="driver";
        }
        ParseUser.getCurrentUser().put("riderOrDriver",userType);
    }

}
