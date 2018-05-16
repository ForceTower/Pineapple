package com.forcetower.uefs.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.GooglePlayGamesInstance;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.ru.RUFirebase;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.service.adapter.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.forcetower.uefs.db.DatabaseMigrations.MIGRATION_1_2;
import static com.forcetower.uefs.db.DatabaseMigrations.MIGRATION_2_3;
import static com.forcetower.uefs.db.DatabaseMigrations.MIGRATION_3_4;
import static com.forcetower.uefs.db.DatabaseMigrations.MIGRATION_4_5;

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
        return Room.databaseBuilder(application, AppDatabase.class, "unes_uefs_5.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build();
    }

    @Provides
    @Singleton
    ServiceDatabase provideServiceDatabase(Application application) {
        return Room.databaseBuilder(application, ServiceDatabase.class, "unes_svc_tst.db")
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

    @Provides
    @Singleton
    RUFirebase provideRUFirebase(Context context) {
        return new RUFirebase(context);
    }


    @Provides
    @Singleton
    GooglePlayGamesInstance provideGooglePlayGamesInstance(Context context) {
        return new GooglePlayGamesInstance(context);
    }

}
