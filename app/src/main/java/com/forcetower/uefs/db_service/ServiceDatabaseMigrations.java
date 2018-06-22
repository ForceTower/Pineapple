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
            database.execSQL("ALTER TABLE Event ADD COLUMN approved INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE Event SET approved = 1");
        }
    };

    public static final Migration MIGRATION_SERVICE_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Timber.d("Executing migration service 3 -> 4");
            database.execSQL("ALTER TABLE Course ADD COLUMN number_of_semesters INTEGER NOT NULL DEFAULT 10");
        }
    };
}
