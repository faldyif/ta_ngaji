package com.preklit.ngaji;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.leakcanary.LeakCanary;

import okhttp3.OkHttpClient;

/**
 * Created by Faldy on 4/17/2018.
 */

public class MyApp extends MultiDexApplication {

    public static final String APP_NAME = "NgajiYuk!";

    @Override
    public void onCreate() {
        super.onCreate();

        // Stetho
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        // LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
