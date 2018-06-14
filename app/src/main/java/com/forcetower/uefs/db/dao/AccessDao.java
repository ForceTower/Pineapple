package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.Access;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface AccessDao {
    @Query("SELECT * FROM Access LIMIT 1")
    Access getAccessDirect();

    @Query("SELECT * FROM Access LIMIT 1")
    LiveData<Access> getAccess();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccess(Access... access);

    @Delete
    void deleteAccess(Access access);

    @Query("DELETE FROM Access")
    void deleteAllAccesses();
}
