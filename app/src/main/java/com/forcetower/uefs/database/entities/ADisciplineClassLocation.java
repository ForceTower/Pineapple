package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
@ForeignKey(entity = ADisciplineGroup.class, parentColumns = "uid", childColumns = "groupId", onUpdate = CASCADE, onDelete = CASCADE)
public class ADisciplineClassLocation {
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

    public ADisciplineClassLocation(int groupId, String startTime, String endTime, String day, String room, String campus, String modulo) {
        this.groupId = groupId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.room = room;
        this.campus = campus;
        this.modulo = modulo;
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
}
