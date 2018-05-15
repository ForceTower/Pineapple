package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

    @Query("SELECT DisciplineClassLocation.* FROM DisciplineClassLocation, DisciplineGroup, Discipline " +
            "WHERE DisciplineClassLocation.class_group IS NOT NULL " +
            "AND DisciplineClassLocation.groupId = DisciplineGroup.uid " +
            "AND DisciplineGroup.discipline = Discipline.uid " +
            "AND Discipline.semester = :semester LIMIT 1")
    LiveData<DisciplineClassLocation> getOneLoadedLocationFromSemester(String semester);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClassLocation(DisciplineClassLocation... classLocations);

    @Delete
    void deleteClassLocation(DisciplineClassLocation classLocation);

    @Query("DELETE FROM DisciplineClassLocation")
    void deleteAllDisciplineClassLocations();

    @Query("DELETE FROM DisciplineClassLocation WHERE groupId IN (SELECT uid FROM DisciplineGroup WHERE discipline IN (SELECT uid FROM Discipline WHERE semester = :semester))")
    void deleteLocationsFromSemester(String semester);
}
