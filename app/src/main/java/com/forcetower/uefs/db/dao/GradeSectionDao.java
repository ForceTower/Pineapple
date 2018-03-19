package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.GradeSection;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface GradeSectionDao {
    @Query("SELECT * FROM GradeSection")
    LiveData<List<GradeSection>> getAllSections();

    @Query("SELECT * FROM GradeSection WHERE discipline = :disciplineId")
    LiveData<List<GradeSection>> getSectionsFromDiscipline(int disciplineId);

    @Query("SELECT * FROM GradeSection WHERE discipline = :disciplineId")
    List<GradeSection> getSectionsFromDisciplineDirect(int disciplineId);

    @Query("SELECT * FROM GradeSection WHERE uid = :sectionId")
    GradeSection getSectionByIdDirect(int sectionId);

    @Query("SELECT * FROM GradeSection WHERE discipline = (SELECT uid FROM Discipline WHERE code = :code AND semester = :semester)")
    LiveData<List<GradeSection>> getSectionsFromDiscipline(String semester, String code);

    @Query("SELECT * FROM GradeSection WHERE discipline = (SELECT uid FROM Discipline WHERE code = :code AND semester = :semester)")
    List<GradeSection> getSectionsFromDisciplineDirect(String semester, String code);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGradeSection(GradeSection... gradeSection);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertGradeSection(GradeSection gradeSection);

    @Delete
    void deleteGradeSection(GradeSection gradeSection);

    @Query("DELETE FROM GradeSection")
    void deleteAllGradeSections();
}
