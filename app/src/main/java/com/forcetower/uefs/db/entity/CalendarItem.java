package com.forcetower.uefs.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
public class CalendarItem {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String day;
    private String message;

    public CalendarItem(String day, String message) {
        this.day = day;
        this.message = message;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarItem that = (CalendarItem) o;

        if (uid != that.uid) return false;
        if (!day.equals(that.day)) return false;
        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + day.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }
}
