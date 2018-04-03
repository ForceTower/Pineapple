package com.forcetower.uefs.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

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

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 2 -> 3");
            database.execSQL("CREATE TABLE IF NOT EXISTS `CalendarEvent` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `calendar_id` TEXT, `event_id` TEXT, `semester` TEXT)");
        }
    };
}
