package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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