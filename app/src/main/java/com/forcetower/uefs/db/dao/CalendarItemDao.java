package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.CalendarItem;

import java.util.List;

@Dao
public interface CalendarItemDao {
    @Query("SELECT * FROM CalendarItem")
    LiveData<List<CalendarItem>> getCalendar();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(CalendarItem... items);

    @Delete
    void deleteItem(CalendarItem item);

    @Query("DELETE FROM CalendarItem")
    void deleteCalendar();
}