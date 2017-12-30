package com.forcetower.uefs;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.forcetower.uefs.dependency_injection.component.ApplicationComponent;
import com.forcetower.uefs.dependency_injection.component.DaggerApplicationComponent;
import com.forcetower.uefs.dependency_injection.module.ApplicationModule;
import com.forcetower.uefs.dependency_injection.module.RoomModule;

/**
 * Created by João Paulo on 09/11/2017.
 */

public class UEFSApplication extends Application {
    private ApplicationComponent component;

    public UEFSApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .roomModule(new RoomModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return component;
    }
}
