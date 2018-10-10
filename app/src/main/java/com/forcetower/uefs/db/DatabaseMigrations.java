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

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 3 -> 4");
            database.execSQL("ALTER TABLE DisciplineClassItem ADD COLUMN class_material_link TEXT");
            database.execSQL("CREATE TABLE IF NOT EXISTS DisciplineClassMaterialLink (uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, class_id INTEGER NOT NULL, name TEXT, link TEXT)");
            Timber.d("Migration 3 -> 4 Executed");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 4 -> 5");
            Timber.d("Migration 4 -> 5 Executed");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 5 -> 6");
            database.execSQL("ALTER TABLE Profile ADD COLUMN course TEXT");
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 6 -> 7");
            database.execSQL("ALTER TABLE TodoItem ADD COLUMN message TEXT");
        }
    };

    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 7 -> 8");
            database.execSQL("CREATE TABLE IF NOT EXISTS SyncRegistry (uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, attempt INTEGER NOT NULL DEFAULT 0, completed INTEGER)");
        }
    };

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 8 -> 9");
            database.execSQL("CREATE TABLE IF NOT EXISTS DisciplineMissedClass (uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, date TEXT, description TEXT, disciplineId INTEGER NOT NULL DEFAULT 0)");
        }
    };

    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 9 -> 10");
            database.execSQL("CREATE TABLE IF NOT EXISTS CourseVariant (uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, uefs_id TEXT, name TEXT, selected INTEGER NOT NULL DEFAULT 0)");
            database.execSQL("CREATE INDEX `index_CourseVariant_uefs_id` ON `CourseVariant` (`uefs_id`)");
        }
    };

    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 10 -> 11");
            database.execSQL("ALTER TABLE Profile ADD COLUMN course_reference INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 11 -> 12");
            database.execSQL("CREATE TABLE IF NOT EXISTS `MessageUNES` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `creator` TEXT NOT NULL, `message` TEXT NOT NULL, `create_date` INTEGER NOT NULL, `image_url` TEXT, `uuid` TEXT NOT NULL)");
            database.execSQL("CREATE UNIQUE INDEX `index_MessageUNES_uuid` ON `MessageUNES` (`uuid`)");
        }
    };

    public static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 12 -> 13");
            database.execSQL("ALTER TABLE MessageUNES ADD COLUMN title TEXT DEFAULT NULL");
        }
    };

    public static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 13 -> 14");
            database.execSQL("ALTER TABLE SyncRegistry ADD COLUMN reason INTEGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_14_15 = new Migration(14, 15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 14 -> 15");
            database.execSQL("ALTER TABLE SyncRegistry ADD COLUMN executor TEXT DEFAULT 'W' NOT NULL");
        }
    };

    public static final Migration MIGRATION_15_16 = new Migration(15, 16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 15 -> 16");
            database.execSQL("ALTER TABLE Message ADD COLUMN receive_time INTEGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_16_17 = new Migration(16, 17) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Timber.d("Executing migration 16 -> 17");
            database.execSQL("DELETE FROM Semester WHERE name LIKE '%g'");
        }
    };
}
