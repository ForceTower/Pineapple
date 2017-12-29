package com.forcetower.uefs.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.forcetower.uefs.database.dao.AAccessDao;
import com.forcetower.uefs.database.entities.AAccess;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Database(entities = {AAccess.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AAccessDao aAccessDao();
}
