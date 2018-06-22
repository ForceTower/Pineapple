package com.forcetower.uefs.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.dao.AccessTokenDao;
import com.forcetower.uefs.db_service.dao.AccountDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.forcetower.uefs.db_service.ServiceDatabaseMigrations.MIGRATION_SERVICE_2_3;
import static com.forcetower.uefs.db_service.ServiceDatabaseMigrations.MIGRATION_SERVICE_3_4;

/**
 * Created by João Paulo on 08/05/2018.
 */
@Module
public class DatabaseServiceModule {

    @Provides
    @Singleton
    ServiceDatabase provideServiceDatabase(Application application) {
        return Room.databaseBuilder(application, ServiceDatabase.class, "uneverse_uefs.db")
                .addMigrations(MIGRATION_SERVICE_2_3, MIGRATION_SERVICE_3_4)
                .build();
    }

    @Provides
    @Singleton
    AccessTokenDao provideAccessTokenDao(ServiceDatabase database) {
        return database.accessTokenDao();
    }

    @Provides
    @Singleton
    AccountDao provideAccountDao(ServiceDatabase database) {
        return database.accountDao();
    }
}
