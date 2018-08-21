package com.forcetower.uefs.db_service.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.forcetower.uefs.db_service.entity.AboutField;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 09/06/2018.
 */
@Dao
public interface AboutFieldDao {
    @Query("SELECT * FROM AboutField")
    LiveData<List<AboutField>> getAbout();

    @Insert(onConflict = REPLACE)
    void insert(List<AboutField> about);

    @Delete
    void delete(AboutField about);

    @Query("DELETE FROM AboutField")
    void deleteAllAbout();
}
