package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.DisciplineClassLocation;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface DisciplineClassLocationDao {
    @Query("SELECT * FROM DisciplineClassLocation")
    LiveData<List<DisciplineClassLocation>> getAllDisciplineClassesLocations();

    @Query("SELECT * FROM DisciplineClassLocation WHERE groupId = :groupId")
    LiveData<List<DisciplineClassLocation>> getDisciplineClassLocations(int groupId);

    @Query("SELECT * FROM DisciplineClassLocation WHERE groupId = :groupId")
    List<DisciplineClassLocation> getDisciplineClassLocationsDirect(int groupId);

    @Query("SELECT * FROM DisciplineClassLocation WHERE groupId IN (SELECT uid FROM DisciplineGroup WHERE discipline IN (SELECT uid FROM Discipline WHERE semester = :semester))")
    LiveData<List<DisciplineClassLocation>> getClassesFromSemester(String semester);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClassLocation(DisciplineClassLocation... classLocations);

    @Delete
    void deleteClassLocation(DisciplineClassLocation classLocation);

    @Query("DELETE FROM DisciplineClassLocation")
    void deleteAllDisciplineClassLocations();

    @Query("DELETE FROM DisciplineClassLocation WHERE groupId IN (SELECT uid FROM DisciplineGroup WHERE discipline IN (SELECT uid FROM Discipline WHERE semester = :semester))")
    void deleteLocationsFromSemester(String semester);
}
