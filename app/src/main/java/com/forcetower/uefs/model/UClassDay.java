package com.forcetower.uefs.model;

import android.support.annotation.NonNull;

import com.forcetower.uefs.helpers.Utils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by João Paulo on 10/11/2017.
 */

public class UClassDay implements Comparable<UClassDay>{
    private String stringStart;
    private String stringFinish;
    private String classType;
    private Calendar start;
    private Calendar finish;
    private String day;
    private String allocatedRoom;
    private String campus;
    private String place;
    private UClass uClass;
    private long duration;

    UClassDay(String startString, String finishString, String day, String classType, UClass uClass) {
        this.stringStart = startString;
        this.stringFinish = finishString;
        this.start = Utils.generateCalendar(startString);
        this.finish = Utils.generateCalendar(finishString);
        this.day = day;
        this.classType = classType;
        this.uClass = uClass;

        duration = Utils.getDateDiff(start.getTime(), finish.getTime(), TimeUnit.HOURS);
    }

    public Calendar getStart() {
        return start;
    }

    public Calendar getFinish() {
        return finish;
    }

    public String getDay() {
        return day;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UClassDay classDay = (UClassDay) o;
        return start.equals(classDay.start) && finish.equals(classDay.finish) && day.equals(classDay.day);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + finish.hashCode();
        result = 31 * result + day.hashCode();
        return result;
    }

    public String getFinishString() {
        return stringFinish;
    }

    public String getStartString() {
        return stringStart;
    }

    public String getAllocatedRoom() {
        return allocatedRoom;
    }

    public void setAllocatedRoom(String allocatedRoom) {
        this.allocatedRoom = allocatedRoom;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public UClass getUClass() {
        return uClass;
    }

    @Override
    public int compareTo(@NonNull UClassDay uClassDay) {
        if (uClassDay.getDay().equalsIgnoreCase(this.getDay())) {
            if (start.before(uClassDay.getStart())) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return Utils.compareDayOfWeek(day, uClassDay.getDay());
        }
    }

    @Override
    public String toString() {
        return "Name: " + uClass.getName() + " - " + stringStart + " ~ " + stringFinish;
    }
}
