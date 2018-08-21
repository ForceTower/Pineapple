package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.SyncRegistry;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 23/06/2018.
 */
@Dao
public interface SyncRegistryDao {
    @Insert(onConflict = REPLACE)
    void insert(SyncRegistry registry);

    @Query("SELECT * FROM SyncRegistry ORDER BY attempt DESC")
    LiveData<List<SyncRegistry>> getAllRegistry();

    @Query("UPDATE SyncRegistry SET completed = :value WHERE uid = " +
            "(SELECT uid from SyncRegistry ORDER BY uid DESC LIMIT 1)")
    void updateSetCompleted(long value);

    @Query("UPDATE SyncRegistry SET reason = :value WHERE uid = " +
            "(SELECT uid from SyncRegistry ORDER BY uid DESC LIMIT 1)")
    void updateReason(int value);

    @Delete
    void delete(SyncRegistry registry);
}
