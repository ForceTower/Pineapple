package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.Discipline;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface DisciplineDao {
    @Query("SELECT * FROM Discipline")
    LiveData<List<Discipline>> getAllDisciplines();

    @Query("SELECT * FROM Discipline")
    List<Discipline> getAllDisciplinesDirect();

    @Query("SELECT * FROM Discipline WHERE uid = :disciplineId")
    Discipline getDisciplinesByIdDirect(int disciplineId);

    @Query("SELECT * FROM Discipline WHERE uid = :disciplineId")
    LiveData<Discipline> getDisciplineById(int disciplineId);

    @Query("SELECT * FROM Discipline WHERE semester = :semester")
    LiveData<List<Discipline>> getDisciplinesFromSemester(String semester);

    @Query("SELECT * FROM Discipline WHERE code = :code")
    LiveData<List<Discipline>> getDisciplinesByCode(String code);

    @Query("SELECT * FROM Discipline WHERE code = :code AND semester = :semester LIMIT 1")
    LiveData<Discipline> getDisciplinesBySemesterAndCode(String semester, String code);

    @Query("SELECT * FROM Discipline WHERE code = :code AND semester = :semester LIMIT 1")
    Discipline getDisciplinesBySemesterAndCodeDirect(String semester, String code);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDiscipline(Discipline... disciplines);

    @Delete
    void deleteDiscipline(Discipline discipline);

    @Query("DELETE FROM Discipline")
    void deleteAllDisciplines();

    @Query("SELECT * FROM Discipline WHERE uid = (SELECT discipline FROM DisciplineGroup WHERE uid = :groupId)")
    LiveData<Discipline> getDisciplineFromGroup(int groupId);
}
