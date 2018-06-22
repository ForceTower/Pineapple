package com.forcetower.uefs.db_service;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

import timber.log.Timber;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class ServiceDatabaseMigrations {

    public static final Migration MIGRATION_SERVICE_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Timber.d("Executing migration service 2 -> 3");
            database.execSQL("ALTER TABLE Event ADD COLUMN approved INTEGER NOT NULL");
            database.execSQL("UPDATE Event SET approved = 1");
        }
    };
}
