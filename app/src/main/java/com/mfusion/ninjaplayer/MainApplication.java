package com.mfusion.ninjaplayer;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by 1B15182 on 21/7/2016 0021.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //debug tool
        Stetho.initializeWithDefaults(this);
    }
}
