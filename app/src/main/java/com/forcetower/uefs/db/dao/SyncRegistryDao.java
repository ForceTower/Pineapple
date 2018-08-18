package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.SyncRegistry;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 23/06/2018.
 */
@Dao
public interface SyncRegistryDao {
    @Insert(onConflict = REPLACE)
    long insert(SyncRegistry registry);

    @Query("SELECT * FROM SyncRegistry ORDER BY attempt DESC")
    LiveData<List<SyncRegistry>> getAllRegistry();

    @Query("UPDATE SyncRegistry SET completed = :value WHERE uid = " +
            "(SELECT uid from SyncRegistry ORDER BY uid DESC LIMIT 1)")
    void updateSetCompleted(long value);

    @Delete
    void delete(SyncRegistry registry);

    @Query("UPDATE SyncRegistry SET reason = :value WHERE uid = " +
            "(SELECT uid from SyncRegistry ORDER BY uid DESC LIMIT 1)")
    void updateReason(int value);
}
