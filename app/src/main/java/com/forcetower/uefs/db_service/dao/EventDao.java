package com.forcetower.uefs.db_service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.forcetower.uefs.db_service.entity.Event;

import java.util.List;

/**
 * Created by João Paulo on 14/06/2018.
 */
@Dao
public abstract class EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Event... events);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Event> events);

    @Query("SELECT * FROM Event WHERE approved = 1 AND course_pointer = :course ORDER BY created_at DESC")
    public abstract LiveData<List<Event>> getAllEvents(int course);

    @Query("SELECT * FROM Event WHERE approved = 1 ORDER BY created_at DESC")
    public abstract LiveData<List<Event>> getAllEvents();

    @Delete
    public abstract void delete(List<Event> events);

    @Query("DELETE FROM Event WHERE approved = 1")
    public abstract void deleteAllApproved();

    @Query("DELETE FROM Event WHERE approved = 0")
    public abstract void deleteAllUnapproved();

    @Transaction
    public void deleteAndInsert(List<Event> events) {
        deleteAllApproved();
        insert(events);
        updateInsertionTime(System.currentTimeMillis()/1000);
    }

    @Query("UPDATE Event SET inserted_at = :createdAt WHERE approved = 1")
    public abstract void updateInsertionTime(long createdAt);

    @Query("UPDATE Event SET inserted_at = :createdAt WHERE approved = 0")
    public abstract void updateInsertionTimeUnapproved(long createdAt);

    @Query("SELECT * FROM Event WHERE approved = 0")
    public abstract LiveData<List<Event>> getAllUnapprovedEvents();

    @Transaction
    public void deleteAndInsertUnapproved(List<Event> events) {
        deleteAllUnapproved();
        insert(events);
        updateInsertionTimeUnapproved(System.currentTimeMillis()/1000);
    }

    @Query("UPDATE Event SET approved = 1 WHERE uuid = :uuid")
    public abstract void markEventApproved(String uuid);

    @Query("SELECT * FROM Event WHERE uuid = :uuid")
    public abstract LiveData<Event> getEvent(String uuid);
}
