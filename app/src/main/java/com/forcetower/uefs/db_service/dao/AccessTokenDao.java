package com.forcetower.uefs.db_service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.forcetower.uefs.db_service.entity.AccessToken;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 30/04/2018.
 */
@Dao
public interface AccessTokenDao {
    @Query("SELECT * FROM AccessToken ORDER BY created_at DESC LIMIT 1")
    LiveData<AccessToken> getAccessToken();

    @Query("SELECT * FROM AccessToken ORDER BY created_at DESC LIMIT 1")
    AccessToken getAccessTokenDirect();

    @Update
    void update(AccessToken token);

    @Insert(onConflict = REPLACE)
    void insert(AccessToken... token);

    @Delete
    void delete(AccessToken token);
}