package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
