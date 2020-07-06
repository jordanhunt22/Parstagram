package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register Parse models
        ParseObject.registerSubclass(Post.class);

        // Set applicationId, and server server based on the values in the Heroku settings.
        // ClientKey is not needed unless explicitly configured
        // Any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jordan-parstagram") // should correspond to APP_ID env variable
                .clientKey("CodePathMoveFastParse")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://jordan-parstagram.herokuapp.com/parse/").build());
    }
}