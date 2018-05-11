package com.forcetower.uefs.di.component;

import android.app.Application;

import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.di.module.ActivitiesModule;
import com.forcetower.uefs.di.module.AppModule;
import com.forcetower.uefs.di.module.DatabaseModule;
import com.forcetower.uefs.di.module.DatabaseServiceModule;
import com.forcetower.uefs.di.module.NetworkModule;
import com.forcetower.uefs.di.module.ReceiversModule;
import com.forcetower.uefs.di.module.ServicesModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by João Paulo on 05/03/2018.
 * Application module, this is the kick start
 */
@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        DatabaseModule.class,
        NetworkModule.class,
        ActivitiesModule.class,
        ServicesModule.class,
        DatabaseServiceModule.class,
        ReceiversModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance Builder application(Application application);
        AppComponent build();
    }

    void inject(UEFSApplication application);
}
