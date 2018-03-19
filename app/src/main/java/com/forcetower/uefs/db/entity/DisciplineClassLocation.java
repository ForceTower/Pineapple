package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
@ForeignKey(entity = DisciplineGroup.class, parentColumns = "uid", childColumns = "groupId", onUpdate = CASCADE, onDelete = CASCADE)
public class DisciplineClassLocation implements Comparable<DisciplineClassLocation> {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int groupId;
    @ColumnInfo(name = "start_time")
    private String startTime;
    @ColumnInfo(name = "end_time")
    private String endTime;
    private String day;
    private String room;
    private String campus;
    private String modulo;
    @ColumnInfo(name = "class_name")
    private String className;

    public DisciplineClassLocation(int groupId, String startTime, String endTime, String day, String room, String campus, String modulo) {
        this.groupId = groupId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.room = room;
        this.campus = campus;
        this.modulo = modulo;
    }

    @Ignore
    public DisciplineClassLocation(String startTime, String endTime, String day, String room, String campus, String modulo, String className) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.room = room;
        this.campus = campus;
        this.modulo = modulo;
        this.className = className;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public int compareTo(@NonNull DisciplineClassLocation o) {
        return getStartTime().compareTo(o.getStartTime());
    }
}
