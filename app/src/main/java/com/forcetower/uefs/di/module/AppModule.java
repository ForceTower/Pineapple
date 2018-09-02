package com.forcetower.uefs.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.forcetower.uefs.GooglePlayGamesInstance;
import com.forcetower.uefs.core.storage.database.EDatabase;
import com.forcetower.uefs.ru.RUFirebase;

import javax.inject.Singleton;

import androidx.room.Room;
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
    RUFirebase provideRUFirebase(Context context) {
        return new RUFirebase(context);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    GooglePlayGamesInstance provideGooglePlayGamesInstance(Context context) {
        return new GooglePlayGamesInstance(context);
    }

    @Provides
    @Singleton
    EDatabase provideEDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), EDatabase.class, "unesco_events__xxx.db")
                .fallbackToDestructiveMigration()
                .build();
    }

}
