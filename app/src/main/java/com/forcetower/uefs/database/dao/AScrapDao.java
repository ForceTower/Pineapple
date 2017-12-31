package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.AScrap;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface AScrapDao {
    @Query("SELECT * FROM AScrap")
    List<AScrap> getAllScraps();

    @Query("SELECT * FROM AScrap WHERE message LIKE :message AND sender LIKE :sender")
    List<AScrap> getScraps(String message, String sender);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScraps(AScrap... scraps);

    @Delete
    void deleteScrap(AScrap scrap);

    @Query("DELETE FROM AScrap")
    void deleteAllScraps();
}
