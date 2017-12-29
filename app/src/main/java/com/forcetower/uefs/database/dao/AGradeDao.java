package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.AGrade;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface AGradeDao {
    @Query("SELECT * FROM AGrade")
    List<AGrade> getAllGrades();

    @Query("SELECT * FROM AGrade WHERE discipline = :disciplineId")
    List<AGrade> getDisciplineGrades(int disciplineId);

    @Query("SELECT * FROM AGrade WHERE discipline = (SELECT uid FROM ADiscipline WHERE code = :code AND semester = :semester)")
    List<AGrade> getDisciplineGrades(String code, String semester);

    @Insert
    void insertDiscipline(AGrade... grades);

    @Delete
    void deleteDiscipline(AGrade grade);

    @Query("DELETE FROM AGrade")
    void deleteAllDisciplines();
}
