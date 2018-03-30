package com.forcetower.uefs.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

import timber.log.Timber;

/**
 * Created by João Paulo on 29/03/2018.
 */
public class DatabaseMigrations {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Timber.d("Executing migration 1 -> 2");
            database.execSQL("ALTER TABLE DisciplineClassLocation ADD COLUMN class_group TEXT");
            database.execSQL("ALTER TABLE DisciplineClassLocation ADD COLUMN class_code TEXT");
        }
    };
}
