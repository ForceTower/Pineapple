package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.Profile;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 06/03/2018.
 */
@Dao
public interface ProfileDao {
    @Insert(onConflict = REPLACE)
    void insertProfile(Profile profile);

    @Query("SELECT * FROM Profile LIMIT 1")
    LiveData<Profile> getProfile();

    @Query("SELECT * FROM Profile LIMIT 1")
    Profile getProfileDirect();

    @Delete
    void deleteProfile(Profile profile);

    @Query("DELETE FROM Profile")
    void deleteAllProfiles();

    @Query("UPDATE Profile SET last_sync_attempt = :timeInMillis")
    void setLastSyncAttempt(long timeInMillis);

    @Query("UPDATE Profile SET course = :name")
    void setProfileCourse(String name);

    @Query("UPDATE Profile SET score = :score")
    void setScore(double score);

    @Query("UPDATE Profile SET course_reference = :courseId")
    void setProfileCourseId(int courseId);
}
