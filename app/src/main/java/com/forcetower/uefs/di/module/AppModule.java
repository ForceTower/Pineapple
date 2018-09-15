package com.forcetower.uefs.di.module;

import android.app.Application;
import android.content.Context;

import com.forcetower.uefs.GooglePlayGamesInstance;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by João Paulo on 05/03/2018.
 *
 * This provides implementations for multiple services needed across the application.
 * It includes the ViewModelModule so it binds the view modules needed as well
 */
@Module(includes = ViewModelModule.class)
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    GooglePlayGamesInstance provideGooglePlayGamesInstance(Context context) {
        return new GooglePlayGamesInstance(context);
    }

}
