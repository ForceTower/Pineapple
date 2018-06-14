package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.CalendarEvent;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 03/04/2018.
 */
@Dao
public interface CalendarEventDao {
    @Insert(onConflict = REPLACE)
    void insertCalendarEvent(CalendarEvent... events);

    @Query("SELECT * FROM CalendarEvent")
    List<CalendarEvent> getExportedCalendarDirect();

    @Query("SELECT * FROM CalendarEvent")
    LiveData<List<CalendarEvent>> getExportedCalendar();

    @Delete
    void deleteCalendarEvent(CalendarEvent event);
}
