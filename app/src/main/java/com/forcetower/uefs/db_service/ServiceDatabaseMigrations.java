package com.forcetower.uefs.db_service;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import timber.log.Timber;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class ServiceDatabaseMigrations {

    public static final Migration MIGRATION_SERVICE_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration service 2 -> 3");
            database.execSQL("ALTER TABLE Event ADD COLUMN approved INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE Event SET approved = 1");
        }
    };

    public static final Migration MIGRATION_SERVICE_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration service 3 -> 4");
            database.execSQL("ALTER TABLE Course ADD COLUMN number_of_semesters INTEGER NOT NULL DEFAULT 10");
        }
    };

    public static final Migration MIGRATION_SERVICE_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration service 4 -> 5");
            database.execSQL("ALTER TABLE Event ADD COLUMN course_pointer INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Course ADD COLUMN service_id INTEGER NOT NULL DEFAULT 1");
        }
    };
}
