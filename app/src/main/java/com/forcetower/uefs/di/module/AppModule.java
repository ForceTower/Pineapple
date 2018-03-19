package com.forcetower.uefs.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.service.adapter.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    AppDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "unes_uefs.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    UNEService provideUNEService(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Constants.UNES_SERVICE_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(client)
                .build()
                .create(UNEService.class);
    }

}
