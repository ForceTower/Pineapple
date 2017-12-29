package com.forcetower.uefs.dependency_injection.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.forcetower.uefs.database.AppDatabase;
import com.forcetower.uefs.database.dao.AAccessDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Module
public class RoomModule {
    private final AppDatabase database;

    public RoomModule(Application application) {
        database = Room.databaseBuilder(application, AppDatabase.class, "room_database.db").build();
    }

    @Provides
    @Singleton
    AppDatabase provideDatabase() {
        return database;
    }

    @Provides
    AAccessDao provideAccessDao() {
        return database.aAccessDao();
    }
}
