package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.CalendarEvent;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

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
