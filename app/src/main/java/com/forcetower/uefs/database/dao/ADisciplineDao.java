package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.ADiscipline;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface ADisciplineDao {
    @Query("SELECT * FROM ADiscipline")
    List<ADiscipline> getAllClasses();
    @Query("SELECT * FROM ADiscipline WHERE semester = :semester")
    List<ADiscipline> getClassesFromSemester(String semester);
}
