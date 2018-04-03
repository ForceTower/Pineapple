package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by João Paulo on 03/04/2018.
 */
@Entity
public class CalendarEvent {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "calendar_id")
    private String calendarId;
    @ColumnInfo(name = "event_id")
    private String eventId;
    private String semester;

    public CalendarEvent(String calendarId, String eventId, String semester) {
        this.calendarId = calendarId;
        this.eventId = eventId;
        this.semester = semester;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
