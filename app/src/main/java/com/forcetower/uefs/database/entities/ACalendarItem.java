package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
public class ACalendarItem {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String day;
    private String message;

    public ACalendarItem(String day, String message) {
        this.day = day;
        this.message = message;
    }

    public int getUid() {
        return uid;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Day: " + day;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ACalendarItem) {
            ACalendarItem o = (ACalendarItem) obj;
            return o.day.equalsIgnoreCase(day) && o.message.equalsIgnoreCase(message);
        }
        return false;
    }
}
