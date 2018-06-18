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

    @Query("SELECT * FROM Event ORDER BY created_at DESC")
    public abstract LiveData<List<Event>> getAllEvents();

    @Delete
    public abstract void delete(List<Event> events);

    @Query("DELETE FROM Event")
    public abstract void deleteAll();

    @Transaction
    public void deleteAndInsert(List<Event> events) {
        deleteAll();
        insert(events);
        updateInsertionTime(System.currentTimeMillis()/1000);
    }

    @Query("UPDATE Event SET inserted_at = :createdAt")
    public abstract void updateInsertionTime(long createdAt);
}
