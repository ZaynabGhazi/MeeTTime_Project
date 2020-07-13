package com.zaynab.meettime;

import android.app.Application;
import android.util.Log;

import com.zaynab.meettime.BuildConfig;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //register parseModels
        //ParseObject.registerSubclass(Post.class);
        //setup parse server
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.APP_ID)
                .clientKey(BuildConfig.MASTER_KEY)
                .server(BuildConfig.SERVER_URL).build());
    }
}
