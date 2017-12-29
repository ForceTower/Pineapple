package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.ADiscipline;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface ADisciplineDao {
    @Query("SELECT * FROM ADiscipline")
    List<ADiscipline> getAllDisciplines();

    @Query("SELECT * FROM ADiscipline WHERE semester = :semester")
    List<ADiscipline> getDisciplinesFromSemester(String semester);

    @Query("SELECT * FROM ADiscipline WHERE code = :code")
    List<ADiscipline> getDisciplinesByCode(String code);

    @Query("SELECT * FROM ADiscipline WHERE code = :code AND semester = :semester")
    List<ADiscipline> getDisciplinesBySemesterAndCode(String semester, String code);

    @Insert
    void insertDiscipline(ADiscipline... disciplines);

    @Delete
    void deleteDiscipline(ADiscipline discipline);

    @Query("DELETE FROM ADiscipline")
    void deleteAllDisciplines();
}
