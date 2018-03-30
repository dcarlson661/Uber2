package com.dcarl661.myuber;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by david carlson_2 on 3/30/2018.
 */

public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
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

    }
}
