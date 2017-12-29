package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.AGradeSection;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface AGradeSectionDao {
    @Query("SELECT * FROM AGradeSection")
    List<AGradeSection> getAllSections();

    @Query("SELECT * FROM AGradeSection WHERE discipline = :disciplineId")
    List<AGradeSection> getSectionsFromDiscipline(int disciplineId);

    @Query("SELECT * FROM AGradeSection WHERE discipline = (SELECT uid FROM ADiscipline WHERE code = :code AND semester = :semester)")
    List<AGradeSection> getSectionsFromDiscipline(String semester, String code);

    @Insert
    void insertGradeSection(AGradeSection... gradeSection);

    @Delete
    void deleteGradeSection(AGradeSection gradeSection);

    @Query("DELETE FROM AGradeSection")
    void deleteAllGradeSections();
}
