package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.ASemester;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface ASemesterDao {
    @Query("SELECT * FROM ASemester WHERE uefs_id = :uefsId")
    ASemester getSemester(String uefsId);

    @Insert
    void insertSemesters(ASemester... semesters);

    @Delete
    void removeSemester(ASemester semester);

    @Query("DELETE FROM ASemester")
    void removeAllSemesters();

    @Query("SELECT * FROM ASemester")
    List<ASemester> getAllSemesters();
}
