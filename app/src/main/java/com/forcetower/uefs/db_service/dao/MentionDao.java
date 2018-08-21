package com.forcetower.uefs.db_service.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
