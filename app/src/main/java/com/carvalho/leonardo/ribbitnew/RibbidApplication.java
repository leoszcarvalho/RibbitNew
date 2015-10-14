package com.carvalho.leonardo.ribbitnew;

import android.app.Application;
import android.util.Log;

import com.carvalho.leonardo.ribbitnew.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

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


        ParseInstallation.getCurrentInstallation().saveInBackground();


        /*Testar funcionamento do Parse
        ParseObject testObject = new ParseObject("TestObject");

        testObject.put("foo", "bar");
        testObject.saveInBackground();
         */
    }

    public static void updateParseInstallation(ParseUser user)
    {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String idObj = user.getObjectId();
        Log.d("Identificacao", idObj);
        installation.put("userId", user);
        installation.put("user", user.getObjectId());

        installation.saveInBackground();
    }

}

