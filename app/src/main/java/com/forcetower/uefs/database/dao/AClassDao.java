package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.AClass;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface AClassDao {
    @Query("SELECT * FROM AClass")
    List<AClass> getAllClasses();
    @Query("SELECT * FROM AClass WHERE semester = :semester")
    List<AClass> getClassesFromSemester(String semester);
}
