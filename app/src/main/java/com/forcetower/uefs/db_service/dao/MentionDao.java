package com.forcetower.uefs.db_service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db_service.entity.Mention;

import java.util.List;

/**
 * Created by João Paulo on 03/06/2018.
 */
@Dao
public interface MentionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Mention... mentions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Mention> mentions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Mention mention);

    @Query("SELECT * FROM Mention")
    LiveData<List<Mention>> getAllMentions();

    @Query("DELETE FROM Mention")
    void deleteAllMentions();

    @Delete
    void delete(Mention mention);
}
