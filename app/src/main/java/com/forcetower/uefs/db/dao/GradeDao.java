package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.Grade;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface GradeDao {
    @Query("SELECT * FROM Grade")
    LiveData<List<Grade>> getAllGrades();

    @Query("SELECT * FROM Grade WHERE discipline = :disciplineId LIMIT 1")
    LiveData<Grade> getDisciplineGrades(int disciplineId);

    @Query("SELECT * FROM Grade WHERE discipline = :disciplineId LIMIT 1")
    Grade getDisciplineGradesDirect(int disciplineId);

    @Query("SELECT * FROM Grade WHERE discipline = (SELECT uid FROM Discipline WHERE code = :code AND semester = :semester)")
    LiveData<Grade> getDisciplineGrades(String code, String semester);

    @Query("SELECT * FROM Grade WHERE discipline = (SELECT uid FROM Discipline WHERE code = :code AND semester = :semester)")
    Grade getDisciplineGradesDirect(String code, String semester);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGrade(Grade... grades);

    @Delete
    void deleteGrade(Grade grade);

    @Query("DELETE FROM Grade")
    void deleteAllGrades();
}
