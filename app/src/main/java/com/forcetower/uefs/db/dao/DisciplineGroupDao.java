package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.DisciplineGroup;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface DisciplineGroupDao {
    @Query("SELECT * FROM DisciplineGroup")
    LiveData<List<DisciplineGroup>> getAllDisciplineGroups();

    @Query("SELECT * FROM DisciplineGroup")
    List<DisciplineGroup> getAllDisciplineGroupsDirect();

    @Query("SELECT * FROM DisciplineGroup WHERE discipline = :disciplineId")
    LiveData<List<DisciplineGroup>> getDisciplineGroups(int disciplineId);

    @Query("SELECT * FROM DisciplineGroup WHERE discipline = :disciplineId")
    List<DisciplineGroup> getDisciplineGroupsDirect(int disciplineId);

    @Query("SELECT * FROM DisciplineGroup WHERE uid = :groupId")
    LiveData<DisciplineGroup> getDisciplineGroupById(int groupId);

    @Query("SELECT * FROM DisciplineGroup WHERE uid = :groupId")
    DisciplineGroup getDisciplineGroupByIdDirect(int groupId);

    @Query("SELECT * FROM DisciplineGroup WHERE discipline = (SELECT uid FROM Discipline WHERE code = :code AND semester = :semester)")
    LiveData<List<DisciplineGroup>> getDisciplineGroups(String semester, String code);

    @Query("SELECT * FROM DisciplineGroup WHERE discipline = (SELECT uid FROM Discipline WHERE code = :code AND semester = :semester)")
    List<DisciplineGroup> getDisciplineGroupsDirect(String semester, String code);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDisciplineGroup(DisciplineGroup... disciplineGroups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDisciplineGroup(DisciplineGroup disciplineGroups);

    @Delete
    void deleteDisciplineGroup(DisciplineGroup disciplineGroup);

    @Query("DELETE FROM DisciplineGroup")
    void deleteAllDisciplineGroups();

    @Query("SELECT * FROM DisciplineGroup WHERE `group` = :group AND discipline = :disciplineId")
    DisciplineGroup getDisciplineGroupByDisciplineIdAndGroupName(int disciplineId, String group);

    @Query("UPDATE DisciplineGroup SET ignored = 1 WHERE uid = :groupId")
    void ignoreGroup(int groupId);

    @Query("UPDATE DisciplineGroup SET ignored = 0 WHERE uid = :groupId")
    void restoreGroup(int groupId);

    @Query("SELECT * FROM DisciplineGroup WHERE draft = 0 AND discipline IN (SELECT uid FROM Discipline WHERE semester = :semester) LIMIT 1")
    LiveData<DisciplineGroup> getLoadedDisciplineFromSemester(String semester);
}
