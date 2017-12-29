package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.AAccess;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface AAccessDao {
    @Query("SELECT * FROM AAccess LIMIT 1")
    AAccess getUser();
    @Insert
    void insertAccess(AAccess... access);
    @Delete
    void deleteAccess(AAccess aAccess);
}