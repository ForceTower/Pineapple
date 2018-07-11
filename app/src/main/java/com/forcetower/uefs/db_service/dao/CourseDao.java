package com.forcetower.uefs.db_service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.forcetower.uefs.db_service.entity.Course;

import java.util.List;

/**
 * Created by João Paulo on 20/06/2018.
 */
@Dao
public abstract class CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Course> courses);

    @Delete
    public abstract void delete(Course course);

    @Transaction
    public void deleteAndInsertAll(List<Course> courses) {
        deleteAll();
        insert(courses);
        updateInsertionTime(System.currentTimeMillis()/1000);
    }

    @Query("DELETE FROM Course")
    public abstract void deleteAll();

    @Query("UPDATE Course SET inserted_at = :createdAt")
    public abstract void updateInsertionTime(long createdAt);

    @Query("SELECT * FROM Course ORDER BY name")
    public abstract LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM Course ORDER BY name")
    public abstract List<Course> getAllCoursesDirect();
}
