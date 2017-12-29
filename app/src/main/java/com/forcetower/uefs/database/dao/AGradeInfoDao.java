package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.AGradeInfo;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface AGradeInfoDao {
    @Query("SELECT * FROM AGradeInfo")
    List<AGradeInfo> getAllGradeInfos();

    @Query("SELECT * FROM AGradeInfo WHERE section = :sectionId")
    List<AGradeInfo> getGradesFromSection(int sectionId);

    @Insert
    void insertGradeInfo(AGradeInfo... gradesInfo);

    @Delete
    void deleteGradeInfo(AGradeInfo gradeInfo);

    @Query("DELETE FROM AGradeInfo")
    void deleteAllGradesInfo();
}
