package com.mfusion.ninjaplayer;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //debug tool
        Stetho.initializeWithDefaults(this);
    }
}
