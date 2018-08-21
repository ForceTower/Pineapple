package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.Semester;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface SemesterDao {
    @Query("SELECT * FROM Semester")
    LiveData<List<Semester>> getAllSemesters();

    @Query("SELECT * FROM Semester")
    List<Semester> getAllSemestersDirect();

    @Query("SELECT * FROM Semester WHERE uefs_id = :uefsId")
    LiveData<Semester> getSemester(String uefsId);

    @Query("SELECT * FROM Semester WHERE name = :name")
    Semester getSemesterByNameDirect(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSemesters(Semester... semesters);

    @Delete
    void removeSemester(Semester semester);

    @Query("DELETE FROM Semester")
    void removeAllSemesters();

    @Query("UPDATE Semester SET uefs_id = :uefsId WHERE name = :semester")
    void setUefsId(String uefsId, String semester);
}
