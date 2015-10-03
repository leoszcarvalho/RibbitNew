package com.carvalho.leonardo.ribbitnew;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Leonardo on 01/10/2015.
 */

public class RibbidApplication extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "T1ugya2I2huxR6zwoBLKEbndhl1nJcL3buWQylGi", "S2HY4XXohFqfpiWTMwhiZ6KpKxF5UKAT6KcebUAF");


        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

    }

}

