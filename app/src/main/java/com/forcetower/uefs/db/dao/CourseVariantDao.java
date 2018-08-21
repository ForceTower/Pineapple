package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.CourseVariant;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CourseVariantDao {
    @Insert(onConflict = REPLACE)
    void insert(List<CourseVariant> variants);

    @Query("SELECT * FROM CourseVariant")
    LiveData<List<CourseVariant>> getAllCourseVariants();

    @Query("SELECT * FROM CourseVariant")
    List<CourseVariant> getAllCourseVariantsDirect();

    @Query("SELECT * FROM CourseVariant WHERE selected = 1 LIMIT 1")
    CourseVariant getSelectedVariantDirect();

    @Query("SELECT * FROM CourseVariant WHERE selected = 1 LIMIT 1")
    LiveData<CourseVariant> getSelectedVariant();

    @Delete
    void delete(List<CourseVariant> variants);
}
