package com.forcetower.uefs.dependency_injection.component;

import android.app.Application;

import com.forcetower.uefs.dependency_injection.module.ApplicationModule;
import com.forcetower.uefs.dependency_injection.module.RoomModule;
import com.forcetower.uefs.view.connected.ConnectedActivity;
import com.forcetower.uefs.view.connected.NConnectedActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class})
public interface ApplicationComponent {
    void inject(ConnectedActivity activity);
    void inject(NConnectedActivity activity);

    Application application();
}
