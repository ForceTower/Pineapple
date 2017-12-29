package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.ADisciplineClassLocation;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface ADisciplineClassLocationDao {
    @Query("SELECT * FROM ADisciplineClassLocation")
    List<ADisciplineClassLocation> getAllDisciplineClassesLocations();

    @Query("SELECT * FROM ADisciplineClassLocation WHERE groupId = :groupId")
    List<ADisciplineClassLocation> getDisciplineClassLocations(int groupId);

    @Insert
    void insertClassLocation(ADisciplineClassLocation... classLocations);

    @Delete
    void deleteClassLocation(ADisciplineClassLocation classLocation);

    @Query("DELETE FROM ADisciplineClassLocation")
    void deleteAllDisciplineClassLocations();
}
