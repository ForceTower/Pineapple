package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.ADisciplineGroup;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface ADisciplineGroupDao {
    @Query("SELECT * FROM ADisciplineGroup")
    List<ADisciplineGroup> getAllDisciplineGroups();

    @Query("SELECT * FROM ADisciplineGroup WHERE discipline = :disciplineId")
    List<ADisciplineGroup> getDisciplineGroups(int disciplineId);

    @Query("SELECT * FROM ADisciplineGroup WHERE discipline = (SELECT uid FROM ADiscipline WHERE code = :code AND semester = :semester)")
    List<ADisciplineGroup> getDisciplineGroups(String semester, String code);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDisciplineGroup(ADisciplineGroup... disciplineGroups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDisciplineGroup(ADisciplineGroup disciplineGroups);

    @Delete
    void deleteDisciplineGroup(ADisciplineGroup disciplineGroup);

    @Query("DELETE FROM ADisciplineGroup")
    void deleteAllDisciplineGroups();
}
