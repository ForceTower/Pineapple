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

    //@Query("SELECT * FROM ADisciplineClassLocation WHERE groupId IN (SELECT uid FROM ADisciplineGroup WHERE discipline IN (SELECT uid FROM ADiscipline WHERE semester = :semester_uefs_id))")
    @Query("SELECT * FROM ADisciplineClassLocation, ADisciplineGroup, ADiscipline " +
            "WHERE ADiscipline.uefs_id = :semester_uefs_id " +
            "AND ADisciplineGroup.discipline = ADiscipline.uid " +
            "AND ADisciplineClassLocation.groupId = ADisciplineGroup.uid")
    List<ADisciplineClassLocation> getClassesFromSemester(String semester_uefs_id);

    @Insert
    void insertClassLocation(ADisciplineClassLocation... classLocations);

    @Delete
    void deleteClassLocation(ADisciplineClassLocation classLocation);

    @Query("DELETE FROM ADisciplineClassLocation")
    void deleteAllDisciplineClassLocations();
}
