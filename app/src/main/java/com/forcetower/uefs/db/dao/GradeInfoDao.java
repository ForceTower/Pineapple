package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.GradeInfo;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface GradeInfoDao {
    @Query("SELECT * FROM GradeInfo")
    LiveData<List<GradeInfo>> getAllGradeInfos();

    @Query("SELECT * FROM GradeInfo WHERE notified = 1")
    List<GradeInfo> getUnnotifiedGrades();

    @Query("SELECT * FROM GradeInfo WHERE notified = 0 AND section IN (SELECT uid FROM GradeSection WHERE discipline IN (SELECT uid FROM Discipline WHERE semester = :semester))")
    List<GradeInfo> getNotifiedGradesFromSemester(String semester);

    @Query("SELECT * FROM GradeInfo WHERE notified = 1 AND section IN (SELECT uid FROM GradeSection WHERE discipline IN (SELECT uid FROM Discipline WHERE semester = :semester))")
    List<GradeInfo> getUnnotifiedGrades(String semester);

    @Query("SELECT * FROM GradeInfo WHERE notified = 2")
    List<GradeInfo> getRecentlyCreatedGrades();

    @Query("SELECT * FROM GradeInfo WHERE notified = 2 AND section IN (SELECT uid FROM GradeSection WHERE discipline IN (SELECT uid FROM Discipline WHERE semester = :semester))")
    List<GradeInfo> getRecentlyCreatedGrades(String semester);

    @Query("SELECT * FROM GradeInfo WHERE section IN (SELECT uid FROM GradeSection WHERE discipline IN (SELECT uid FROM Discipline WHERE semester = :semester))")
    LiveData<List<GradeInfo>> getAllGradesFromSemester(String semester);

    @Query("SELECT * FROM GradeInfo WHERE notified = 3")
    List<GradeInfo> getAvDateChangedGrades();

    @Query("SELECT * FROM GradeInfo WHERE notified = 3 AND section IN (SELECT uid FROM GradeSection WHERE discipline IN (SELECT uid FROM Discipline WHERE semester = :semester))")
    List<GradeInfo> getAvDateChangedGrades(String semester);

    @Query("SELECT * FROM GradeInfo WHERE section = :sectionId")
    LiveData<List<GradeInfo>> getGradesFromSection(int sectionId);

    @Query("SELECT * FROM GradeInfo WHERE section = :sectionId")
    List<GradeInfo> getGradesFromSectionDirect(int sectionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGradeInfo(GradeInfo... gradesInfo);

    @Delete
    void deleteGradeInfo(GradeInfo gradeInfo);

    @Query("DELETE FROM GradeInfo")
    void deleteAllGradesInfo();

    @Query("UPDATE GradeInfo SET notified = 0")
    void clearAllNotifications();
}
