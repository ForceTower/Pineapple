package com.forcetower.uefs.db_service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db_service.entity.AboutField;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

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
