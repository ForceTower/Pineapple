package com.forcetower.uefs;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by João Paulo on 09/11/2017.
 */

public class UEFSApplication extends Application {

    public UEFSApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
