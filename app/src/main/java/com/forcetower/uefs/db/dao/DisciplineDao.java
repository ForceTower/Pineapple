package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.helper.DisciplineAndGrade;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface DisciplineDao {
    @Query("SELECT * FROM Discipline")
    LiveData<List<Discipline>> getAllDisciplines();

    @Transaction
    @Query("SELECT * FROM Discipline")
    LiveData<List<DisciplineAndGrade>> getAllDisciplinesWithGrades();

    @Query("SELECT * FROM Discipline")
    List<Discipline> getAllDisciplinesDirect();

    @Query("SELECT * FROM Discipline WHERE uid = :disciplineId")
    Discipline getDisciplinesByIdDirect(int disciplineId);

    @Query("SELECT * FROM Discipline WHERE uid = :disciplineId")
    LiveData<Discipline> getDisciplineById(int disciplineId);

    @Query("SELECT * FROM Discipline WHERE semester = :semester")
    LiveData<List<Discipline>> getDisciplinesFromSemester(String semester);

    @Query("SELECT * FROM Discipline WHERE semester = :semester")
    List<Discipline> getDisciplinesFromSemesterDirect(String semester);

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
