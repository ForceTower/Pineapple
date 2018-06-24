package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.DisciplineMissedClass;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 23/06/2018.
 */
@Dao
public interface DisciplineMissedClassesDao {
    @Insert(onConflict = REPLACE)
    void insert(List<DisciplineMissedClass> items);

    @Query("SELECT DisciplineMissedClass.* FROM DisciplineMissedClass, Semester, Discipline WHERE " +
            "DisciplineMissedClass.disciplineId = Discipline.uid AND " +
            "Discipline.code = :disciplineCode AND " +
            "Semester.name = :semester")
    LiveData<List<DisciplineMissedClass>> getMissedClassesOfDiscipline(String disciplineCode, String semester);

    @Query("SELECT DisciplineMissedClass.* FROM DisciplineMissedClass, Semester, Discipline WHERE " +
            "DisciplineMissedClass.disciplineId = Discipline.uid AND " +
            "Discipline.code = :disciplineCode AND " +
            "Semester.name = :semester")
    List<DisciplineMissedClass> getMissedClassesOfDisciplineDirect(String disciplineCode, String semester);

    @Query("SELECT * FROM DisciplineMissedClass WHERE disciplineId = :disciplineId")
    LiveData<List<DisciplineMissedClass>> getMissedClassesOfDiscipline(int disciplineId);

    @Query("DELETE FROM DisciplineMissedClass WHERE uid IN (" +
            "SELECT DisciplineMissedClass.uid FROM DisciplineMissedClass, Semester, Discipline WHERE " +
            "DisciplineMissedClass.disciplineId = Discipline.uid AND " +
            "Discipline.code = :disciplineCode AND " +
            "Semester.name = :semester)")
    void deleteFromDiscipline(String disciplineCode, String semester);

    @Delete
    void deleteAll(List<DisciplineMissedClass> items);

    @Delete
    void delete(DisciplineMissedClass item);
}
