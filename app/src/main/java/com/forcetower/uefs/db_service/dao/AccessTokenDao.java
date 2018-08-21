package com.forcetower.uefs.db_service.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.forcetower.uefs.db_service.entity.AccessToken;

import static androidx.room.OnConflictStrategy.REPLACE;

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

    @Query("DELETE FROM AccessToken")
    void deleteAll();
}